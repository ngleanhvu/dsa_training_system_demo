package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.*;
import com.ngleanhvu.dsa_training_system.dto.response.*;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.SubmissionMapper;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.repo.spec.SubmissionSpecification;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final ProgrammingLanguageRepo programmingLanguageRepo;
    private final ProblemDetailRepo problemDetailRepo;
    private final RestTemplate restTemplate;
    private final TestCaseRepo testCaseRepo;
    private final ProblemRepo problemRepo;
    private final SubmissionRepo submissionRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ContestProblemRepo contestProblemRepo;

    @Value("${piston.load_balancer}")
    private String loadBalancer;

    @Value("${piston.port}")
    private int port;

    @Override
    public ListSubmissionResponse submit(SubmissionRequest submissionRequest) throws JsonProcessingException {
        ProgrammingLanguage programmingLanguage = programmingLanguageRepo.findById(submissionRequest.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", String.valueOf(submissionRequest.getLanguageId())));

        ProblemDetail problemDetail = problemDetailRepo.findByProblemId(submissionRequest.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("ProblemDetail", "id", String.valueOf(submissionRequest.getProblemId())));

        List<TestCase> testCases = testCaseRepo.findAllByProblemId(submissionRequest.getProblemId());

        if (testCases.isEmpty()) {
            throw new InvalidValueException("Test case quantity must be more than 1");
        }

        log.info("load balancer: {}", loadBalancer);
        log.info("port: {}", port);

        List<SubmissionResponse> allResponses = new ArrayList<>();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<SubmissionResponse>> futures = new ArrayList<>();
            long memoryLimit = AppUtil.megabytesToBytes(problemDetail.getMemoryLimit());
            for (TestCase testCase : testCases) {
                futures.add(executor.submit(() -> {

                    Map<String, Object> params = new HashMap<>();
                    params.put("language", programmingLanguage.getFileName());
                    params.put("version", programmingLanguage.getVersion());

                    List<Map<String, String>> files = new ArrayList<>();
                    Map<String, String> file = new HashMap<>();
                    file.put("name", programmingLanguage.getFileMainName());
                    file.put("content", submissionRequest.getSourceCode());
                    files.add(file);
                    params.put("files", files);

                    params.put("stdin", testCase.getInput());
                    params.put("run_timeout", problemDetail.getTimeLimit());
                    params.put("run_memory_limit", problemDetail.getMemoryLimit());

                    log.info("params: {}", params);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

                    String URL = String.format("http://%s:%d/api/v2/execute", loadBalancer, port);

                    SubmissionResponse response = executeCode(URL, request, testCase);

                    if (response == null) {
                        return SubmissionResponse.builder()
                                .status(SubmissionStatus.NULL_RESPONSE)

                                .input(testCase.getInput())
                                .expectOutput(testCase.getOutput())
                                .build();
                    }

                    response.setInput(testCase.getInput());
                    response.setExpectOutput(testCase.getOutput());

                    String actualOutput = response.getRun().getStdout().trim().replace("\r", "");
                    log.info("Actual output: {}", actualOutput);
                    String expectedOutput = testCase.getOutput().trim().replace("\r", "");
                    log.info("Expected output: {}", expectedOutput);

                    Integer memoryUsed = response.getRun().getMemory();

                    long timeUsed = response.getRun().getCpuTime();
                    log.info("Time used: {}", timeUsed);
                    long timeLimit = problemDetail.getTimeLimit();
                    log.info("Time limit: {}", timeLimit);

                    log.info("response: {}", response);

                    System.out.println(response.getRun() == null);

                    if (response.getRun() == null) {
                        response.setStatus(SubmissionStatus.ERROR);
                    } else if (response.getRun().getCode() != null && response.getRun().getCode() != 0) {
                        response.setStatus(SubmissionStatus.COMPILE_ERROR);
                    } else if (response.getRun().getStatus() != null && response.getRun().getStatus().equals("TO")) {
                        response.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    } else if (memoryUsed > memoryLimit) {
                        response.setStatus(SubmissionStatus.MEMORY_LIMIT_EXCEEDED);
                    } else if (!actualOutput.equals(expectedOutput)) {
                        response.setStatus(SubmissionStatus.WRONG_ANSWER);
                    } else {
                        response.setStatus(SubmissionStatus.ACCEPTED);
                    }

                    response.setTestCaseId(testCase.getTestCaseId());
                    return response;
                }));
            }

            for (Future<SubmissionResponse> future : futures) {
                try {
                    allResponses.add(future.get(10, TimeUnit.SECONDS));
                } catch (TimeoutException e) {
                    SubmissionResponse timeoutResp = new SubmissionResponse();
                    timeoutResp.setStatus(SubmissionStatus.TIMEOUT);
                    allResponses.add(timeoutResp);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        int maxCpuTime = getMaxCpuTime(allResponses);
        log.info("Avg CPU Time: {} ms", maxCpuTime);

        int maxMemory = getMaxMemory(allResponses);
        log.info("Max Memory: {} ms", maxMemory);

        OverallResponse overallResponse = evaluateOverallStatus(allResponses);
        log.info("Overall Response: {}", overallResponse);

        ListSubmissionResponse listSubmissionResponse = ListSubmissionResponse.builder()
                .submissionResponses(allResponses)
                .memory(maxMemory)
                .runtime(maxCpuTime)
                .language(programmingLanguage.getName())
                .sourcecode(submissionRequest.getSourceCode())
                .status(overallResponse)
                .build();

        List<SubmissionTestCaseCreateRequest> submissionTestCaseCreateRequests = allResponses.stream()
                .map(r -> SubmissionTestCaseCreateRequest.builder()
                        .testCaseId(r.getTestCaseId())
                        .runtime(r.getRun().getCpuTime())
                        .memory(r.getRun().getMemory())
                        .status(r.getStatus())
                        .build())
                .toList();

        log.info("submissionTestCaseCreateRequests: {}", submissionTestCaseCreateRequests);

        SubmissionCreateRequest submissionCreateRequest = SubmissionCreateRequest.builder()
                .submissionTestCaseCreateRequests(submissionTestCaseCreateRequests)
                .pass(overallResponse.getPass())
                .contestId(submissionRequest.getContestId())
                .total(overallResponse.getTotal())
                .programmingLanguageId(programmingLanguage.getProgrammingLanguageId())
                .problemId(problemDetail.getProblem().getProblemId())
                .sourceCode(submissionRequest.getSourceCode())
                .submitTime(LocalDateTime.now())
                .runtime(maxCpuTime)
                .memory(maxMemory)
                .message(overallResponse.getMessage())
                .status(overallResponse.getStatus())
                .build();

        String submissionJson = objectMapper.writeValueAsString(submissionCreateRequest);

        log.info("json: {}", submissionJson);

        kafkaTemplate.send(KafkaConst.SUBMISSION_CREATE_TOPIC, submissionJson);

        return listSubmissionResponse;
    }

    @KafkaListener(topics = KafkaConst.SUBMISSION_CREATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    @Override
    @Transactional
    public void createSubmission(String json) {
        try {
            SubmissionCreateRequest submissionCreateRequest = objectMapper.readValue(json, SubmissionCreateRequest.class);

            log.info("submissionCreateRequest: {}", submissionCreateRequest);

            ProgrammingLanguage programmingLanguage = programmingLanguageRepo.findById(submissionCreateRequest.getProgrammingLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", String.valueOf(submissionCreateRequest.getProgrammingLanguageId())));

            Problem problem = problemRepo.findById(submissionCreateRequest.getProblemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(submissionCreateRequest.getProblemId())));

            SubmissionStatus statusEnum = AppUtil.fromValue(submissionCreateRequest.getStatus());

            SubmissionCountProjection count = submissionRepo.countSubmissionByProblemAndStatus(
                    SubmissionStatus.ACCEPTED, problem.getProblemId()
            );

            long totalSubmission = (count.getTotalSubmission() != null ? count.getTotalSubmission() : 0L) + 1;
            long totalAccepted = count.getTotalAccepted() != null ? count.getTotalAccepted() : 0L;

            if (statusEnum == SubmissionStatus.ACCEPTED) {
                totalAccepted += 1;
            }

            log.info("totalSubmission: {}", totalSubmission);
            log.info("totalAccepted: {}", totalAccepted);

            double acceptRate = ((double) totalAccepted / totalSubmission) * 100;
            BigDecimal roundedRate = BigDecimal.valueOf(acceptRate).setScale(2, RoundingMode.HALF_UP);
            acceptRate = roundedRate.doubleValue();

            Submission submission = Submission.builder()
                    .submissionStatus(statusEnum)
                    .code(submissionCreateRequest.getSourceCode())
                    .memoryKb(submissionCreateRequest.getMemory())
                    .errorMessage(submissionCreateRequest.getMessage())
                    .runtimeMs(submissionCreateRequest.getRuntime())
                    .testCasesPassed(submissionCreateRequest.getPass())
                    .totalTestCases(submissionCreateRequest.getTotal())
                    .programmingLanguage(programmingLanguage)
                    .problem(problem)
                    .submittedAt(submissionCreateRequest.getSubmitTime())
                    .status(1)
                    .build();

            submission = submissionRepo.save(submission);

            SubmissionTestCaseRequest submissionTestCaseCreateRequest = SubmissionTestCaseRequest.builder()
                    .submissionTestCaseCreateRequests(submissionCreateRequest.getSubmissionTestCaseCreateRequests())
                    .submissionId(submission.getSubmissionId())
                    .build();

            ProblemDocumentUpdateAcceptRateRequest problemDocumentUpdateAcceptRateRequest = ProblemDocumentUpdateAcceptRateRequest.builder()
                    .acceptRate(acceptRate)
                    .problemId(problem.getProblemId())
                    .build();

            if (submissionCreateRequest.getContestId() != null) {

                ContestProblem contestProblem = contestProblemRepo.findByProblemId(problem.getProblemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(submissionCreateRequest.getProblemId())));

                ContestSubmissionCreateRequest contestSubmissionCreateRequest = ContestSubmissionCreateRequest.builder()
                        .submissionId(submission.getSubmissionId())
                        .contestId(submissionCreateRequest.getContestId())
                        .score(submissionCreateRequest.getTotal() == submissionCreateRequest.getPass() ? contestProblem.getScore() : 0)
                        .build();

                log.info("contestSubmissionCreateRequest: {}", contestSubmissionCreateRequest);

                String contestSubmissionJson = objectMapper.writeValueAsString(contestSubmissionCreateRequest);
                kafkaTemplate.send(KafkaConst.CONTEST_SUBMISSION_CREATE_TOPIC, contestSubmissionJson);

            }

            log.info("problemDocumentUpdateAcceptRateRequest: {}", problemDocumentUpdateAcceptRateRequest);

            String submissionTestCaseRequestJson = objectMapper.writeValueAsString(submissionTestCaseCreateRequest);
            String problemDocumentUpdateAcceptRateRequestJson = objectMapper.writeValueAsString(problemDocumentUpdateAcceptRateRequest);

            kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_UPDATE_ACCEPT_RATE_TOPIC, submissionTestCaseRequestJson, problemDocumentUpdateAcceptRateRequestJson);
            kafkaTemplate.send(KafkaConst.SUBMISSION_TEST_CASE_CREATE_TOPIC, submissionTestCaseRequestJson);
        } catch (Exception e) {
            log.error("Kafka message xử lý thất bại: {}", json, e);
        }
    }

    @Override
    public List<BasicResultSubmissionResponse> getBasicSubmissionResponses(String userId, int problemId) {
        List<Submission> submissions = submissionRepo.getSubmissionByUserIdAndProblemId(userId, problemId);

        if (submissions.isEmpty()) {
            return Collections.emptyList();
        }

        List<BasicResultSubmissionResponse> responses = submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList();

        log.info("responses: {}", responses);

        return responses;
    }

    @Override
    public ListBasicResultSubmissionResponse getBasicSubmissionResponses(SubmissionFilterRequest filterRequest, PagingSearch pagingSearch) {
        log.info("filterRequest: {}", filterRequest);
        Specification<Submission> specification = SubmissionSpecification.hasProblemIdInRange(filterRequest.getProblemId())
                .and(SubmissionSpecification.hasSubmissionStatuses(filterRequest.getStatus()))
                .and(SubmissionSpecification.hasTimeRange(filterRequest.getTimeRange()))
                .and(SubmissionSpecification.hasProgrammingLanguages(filterRequest.getProgrammingLanguageId()));

        Page<Submission> submissions = submissionRepo.findAll(specification, pagingSearch.toPageable());

        log.info("submissions: {}", submissions.getTotalElements());

        if (submissions.isEmpty()) {
            log.info("submissions is empty");
            return null;
        }

        int page = submissions.getNumber() + 1;
        int totalPages = submissions.getTotalPages();

        List<BasicResultSubmissionResponse> basicResultSubmissionResponses = submissions.getContent().stream()
                .map(SubmissionMapper::toDto)
                .toList();

        log.info("basicResultSubmissionResponses: {}", basicResultSubmissionResponses);

        return ListBasicResultSubmissionResponse.builder()
                .submissions(basicResultSubmissionResponses)
                .totalPages(totalPages)
                .page(page)
                .build();
    }


    private static int getMaxCpuTime(List<SubmissionResponse> responses) {
        return (int) responses.stream()
                .filter(r -> r.getRun() != null)
                .mapToLong(r -> r.getRun().getCpuTime())
                .max()
                .orElse(0);
    }

    private static int getMaxMemory(List<SubmissionResponse> responses) {
        return (int) responses.stream()
                .filter(r -> r.getRun() != null)
                .mapToLong(r -> r.getRun().getMemory())
                .max()
                .orElse(0);
    }

    private OverallResponse evaluateOverallStatus(List<SubmissionResponse> responses) {
        int total = responses.size();
        int pass = 0;

        for (SubmissionResponse r : responses) {
            var run = r.getRun();

            if (run != null && run.getCode() != null && run.getCode() != 0) {
                return OverallResponse.builder()
                        .status(SubmissionStatus.COMPILE_ERROR.getValue())
                        .message(run.getStderr())
                        .pass(0).total(0).build();
            }

            if (run != null && "RE".equals(run.getStatus())) {
                return OverallResponse.builder()
                        .status(SubmissionStatus.RUNTIME_ERROR.getValue())
                        .message(run.getStderr())
                        .pass(0).total(0).build();
            }

            if (run != null && "TO".equals(run.getStatus())) {
                return OverallResponse.builder()
                        .status(SubmissionStatus.TIME_LIMIT_EXCEEDED.getValue())
                        .message(run.getStderr())
                        .pass(0).total(0).build();
            }

            if (SubmissionStatus.ACCEPTED == r.getStatus()) {
                pass++;
            }
        }


        if (pass == total) {
            return OverallResponse.builder()
                    .status(SubmissionStatus.ACCEPTED.getValue())
                    .message(null)
                    .pass(pass)
                    .total(total)
                    .build();
        }

        return OverallResponse.builder()
                .status(SubmissionStatus.WRONG_ANSWER.getValue())
                .message(null)
                .pass(pass)
                .total(total)
                .build();
    }

    private SubmissionResponse executeCode(String url, HttpEntity<Map<String, Object>> request, TestCase testCase) {
        try {
            SubmissionResponse response = restTemplate.postForObject(url, request, SubmissionResponse.class);
            if (response == null) {
                return SubmissionResponse.builder()
                        .status(SubmissionStatus.NULL_RESPONSE)
                        .input(testCase.getInput())
                        .expectOutput(testCase.getOutput())
                        .build();
            }
            return response;
        } catch (Exception e) {
            log.error("Error while submit: {}", e.getMessage());
            return SubmissionResponse.builder()
                    .status(SubmissionStatus.ERROR)
                    .input(testCase.getInput())
                    .expectOutput(testCase.getOutput())
                    .build();
        }
    }



}
