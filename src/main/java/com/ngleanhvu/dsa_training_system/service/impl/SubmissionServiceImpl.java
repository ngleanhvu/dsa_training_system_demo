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
import com.ngleanhvu.dsa_training_system.websocket.WebSocketPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final WebSocketPublisher webSocketPublisher;
    private final UserRepo userRepo;

    @Value("${piston.load_balancer}")
    private String loadBalancer;

    @Value("${piston.port}")
    private int port;

    @Override
    public ListSubmissionResponse submit(SubmissionRequest submissionRequest) throws JsonProcessingException {
        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findById(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getName()));
        String email = user.getEmail();

        ProgrammingLanguage programmingLanguage = programmingLanguageRepo.findById(submissionRequest.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", String.valueOf(submissionRequest.getLanguageId())));

        ProblemDetail problemDetail = problemDetailRepo.findByProblemId(submissionRequest.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("ProblemDetail", "id", String.valueOf(submissionRequest.getProblemId())));

        List<TestCase> testCases = testCaseRepo.findAllByProblemId(submissionRequest.getProblemId());
        if (testCases.isEmpty()) {
            throw new InvalidValueException("Test case quantity must be more than 1");
        }
        long memoryLimitBytes = AppUtil.megabytesToBytes(problemDetail.getMemoryLimit());
        Map<String, Object> initPayload = Map.of(
                "type", "INIT",
                "testCases", testCases.stream()
                        .map(stc -> Map.of(
                                "testCaseId", stc.getTestCaseId(),
                                "status", SubmissionStatus.PENDING.name()
                        ))
                        .toList()
        );
        webSocketPublisher.sendSubmissionUpdate(email, initPayload);
        ExecutorService executor;
        try {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } catch (Throwable t) {
            log.warn("Virtual threads not available, fallback to cached thread pool");
            executor = Executors.newCachedThreadPool();
        }
        Map<Integer, Future<SubmissionResponse>> futureByTestId = new LinkedHashMap<>();
        List<SubmissionResponse> allResponses = Collections.synchronizedList(new ArrayList<>());
        for (TestCase testCase : testCases) {
            Map<String, Object> runningPayload = Map.of(
                    "type", "TESTCASE_STATUS",
                    "testCaseId", testCase.getTestCaseId(),
                    "status", "RUNNING"
            );
            webSocketPublisher.sendSubmissionUpdate(email, runningPayload);

            Future<SubmissionResponse> future = executor.submit(() -> {
                SubmissionResponse resp = runSingleTestCase(
                        programmingLanguage, problemDetail, submissionRequest, testCase, memoryLimitBytes, problemDetail.getTimeLimit()
                );
                Map<String, Object> donePayload = new HashMap<>();
                donePayload.put("type", "TESTCASE_STATUS");
                donePayload.put("testCaseId", testCase.getTestCaseId());
                donePayload.put("status", resp.getStatus().name());
                donePayload.put("input", resp.getInput());
                donePayload.put("expectOutput", resp.getExpectOutput());
                donePayload.put("actualOutput", resp.getRun() != null ? resp.getRun().getStdout() : null);
                donePayload.put("stderr", resp.getRun() != null ? resp.getRun().getStderr() : null);
                donePayload.put("cpuTime", resp.getRun() != null ? resp.getRun().getCpuTime() : 0);
                donePayload.put("memory", resp.getRun() != null ? AppUtil.bytesToMegabytes(resp.getRun().getMemory()) : 0);
                webSocketPublisher.sendSubmissionUpdate(email, donePayload);
                allResponses.add(resp);
                return resp;
            });

            futureByTestId.put(testCase.getTestCaseId(), future);
        }

        int perCaseTimeoutSeconds = 10;
        for (Map.Entry<Integer, Future<SubmissionResponse>> entry : futureByTestId.entrySet()) {
            Integer testCaseId = entry.getKey();
            Future<SubmissionResponse> future = entry.getValue();

            try {
                future.get(perCaseTimeoutSeconds, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                log.warn("Test case {} timeout", testCaseId);

                Map<String, Object> timeoutPayload = Map.of(
                        "type", "TESTCASE_STATUS",
                        "testCaseId", testCaseId,
                        "status", SubmissionStatus.TIMEOUT.name(),
                        "message", "Test case timeout"
                );
                webSocketPublisher.sendSubmissionUpdate(email, timeoutPayload);

                SubmissionResponse toResp = SubmissionResponse.builder()
                        .status(SubmissionStatus.TIMEOUT)
                        .testCaseId(testCaseId)
                        .build();
                allResponses.add(toResp);

                future.cancel(true);
            } catch (Exception e) {
                log.error("Error while waiting for test case {}: {}", testCaseId, e.getMessage(), e);

                Map<String, Object> errorPayload = Map.of(
                        "type", "TESTCASE_STATUS",
                        "testCaseId", testCaseId,
                        "status", SubmissionStatus.ERROR.name(),
                        "message", e.getMessage()
                );
                webSocketPublisher.sendSubmissionUpdate(email, errorPayload);

                SubmissionResponse errResp = SubmissionResponse.builder()
                        .status(SubmissionStatus.ERROR)
                        .testCaseId(testCaseId)
                        .build();
                allResponses.add(errResp);
            }
        }

        executor.shutdownNow();

        OverallResponse overallResponse = evaluateOverallStatus(allResponses);

        ListSubmissionResponse listSubmissionResponse = ListSubmissionResponse.builder()
                .submissionResponses(new ArrayList<>(allResponses))
                .memory(getMaxMemory(allResponses))
                .runtime(getMaxCpuTime(allResponses))
                .language(programmingLanguage.getName())
                .sourcecode(submissionRequest.getSourceCode())
                .status(overallResponse)
                .build();

        Map<String, Object> finalPayload = Map.of(
                "type", "FINAL",
                "overall", overallResponse,
                "memory", listSubmissionResponse.getMemory(),
                "runtime", listSubmissionResponse.getRuntime(),
                "language", listSubmissionResponse.getLanguage()
        );
        webSocketPublisher.sendSubmissionUpdate(email, finalPayload);

        List<SubmissionTestCaseCreateRequest> submissionTestCaseCreateRequests = allResponses.stream()
                .map(r -> SubmissionTestCaseCreateRequest.builder()
                        .testCaseId(r.getTestCaseId())
                        .runtime(r.getRun() != null ? r.getRun().getCpuTime() : 0)
                        .memory(r.getRun() != null ? AppUtil.bytesToMegabytes(r.getRun().getMemory()) : 0)
                        .status(r.getStatus())
                        .build())
                .toList();
        log.info("submission test case create: {}", submissionTestCaseCreateRequests);

        SubmissionCreateRequest submissionCreateRequest = SubmissionCreateRequest.builder()
                .submissionTestCaseCreateRequests(submissionTestCaseCreateRequests)
                .pass(overallResponse.getPass())
                .contestId(submissionRequest.getContestId())
                .total(overallResponse.getTotal())
                .programmingLanguageId(programmingLanguage.getProgrammingLanguageId())
                .problemId(problemDetail.getProblem().getProblemId())
                .sourceCode(submissionRequest.getSourceCode())
                .submitTime(LocalDateTime.now())
                .runtime(listSubmissionResponse.getRuntime())
                .memory(listSubmissionResponse.getMemory())
                .message(overallResponse.getMessage())
                .status(overallResponse.getStatus())
                .userId(user.getUserId())
                .build();

        String submissionJson = objectMapper.writeValueAsString(submissionCreateRequest);
        kafkaTemplate.send(KafkaConst.SUBMISSION_CREATE_TOPIC, submissionJson);

        return listSubmissionResponse;
    }


    private SubmissionResponse runSingleTestCase(
            ProgrammingLanguage programmingLanguage,
            ProblemDetail problemDetail,
            SubmissionRequest submissionRequest,
            TestCase testCase,
            long memoryLimitBytes,
            int timeLimit
    ) {
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
        params.put("run_memory_limit", problemDetail.getMemoryLimit());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
        String URL = String.format("http://%s:%d/api/v2/execute", loadBalancer, port);
        SubmissionResponse response = executeCode(URL, request, testCase);
        log.info("Submission response: {}", response);
        log.info("test case: {}", testCase.getTestCaseId());
        if (response == null) {
            return SubmissionResponse.builder()
                    .status(SubmissionStatus.NULL_RESPONSE)
                    .input(testCase.getInput())
                    .expectOutput(testCase.getOutput())
                    .testCaseId(testCase.getTestCaseId())
                    .build();
        }
        response.setInput(testCase.getInput());
        response.setExpectOutput(testCase.getOutput());
        response.setTestCaseId(testCase.getTestCaseId());
        if (response.getRun() == null) {
            response.setStatus(SubmissionStatus.ERROR);
            return response;
        }
        if (response.getRun().getCode() != null && response.getRun().getCode() != 0) {
            response.setStatus(SubmissionStatus.COMPILE_ERROR);
            return response;
        }
        if ("RE".equals(response.getRun().getStatus())) {
            response.setStatus(SubmissionStatus.RUNTIME_ERROR);
            return response;
        }
        double memoryUsed = response.getRun().getMemory();
        int timeUsed = response.getRun().getCpuTime();
        log.info("timeUsed: {}", timeUsed);
        log.info("time limit: {}", timeLimit);
        if (memoryUsed > memoryLimitBytes) {
            response.setStatus(SubmissionStatus.MEMORY_LIMIT_EXCEEDED);
            return response;
        }
        if (timeUsed > timeLimit) {
            response.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
            return response;
        }
        String actualOutput = response.getRun().getStdout() == null ? "" : response.getRun().getStdout().trim().replace("\r", "");
        String expectedOutput = testCase.getOutput() == null ? "" : testCase.getOutput().trim().replace("\r", "");
        if (!actualOutput.equals(expectedOutput)) {
            response.setStatus(SubmissionStatus.WRONG_ANSWER);
        } else {
            response.setStatus(SubmissionStatus.ACCEPTED);
        }
        return response;
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
            log.info("submission response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error while submit: {}", e.getMessage(), e);
            return SubmissionResponse.builder()
                    .status(SubmissionStatus.ERROR)
                    .input(testCase.getInput())
                    .expectOutput(testCase.getOutput())
                    .build();
        }
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

            User user = userRepo.findById(submissionCreateRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", String.valueOf(submissionCreateRequest.getUserId())));

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
                    .user(user)
                    .status(1)
                    .build();

            submission = submissionRepo.save(submission);

            SubmissionTestCaseRequest submissionTestCaseCreateRequest = SubmissionTestCaseRequest.builder()
                    .submissionTestCaseCreateRequests(submissionCreateRequest.getSubmissionTestCaseCreateRequests())
                    .submissionId(submission.getSubmissionId())
                    .build();
            log.info("submissionTestCaseCreateRequest: {}", submissionTestCaseCreateRequest);

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

                String contestSubmissionJson = objectMapper.writeValueAsString(contestSubmissionCreateRequest);
                kafkaTemplate.send(KafkaConst.CONTEST_SUBMISSION_CREATE_TOPIC, contestSubmissionJson);
            }

            String submissionTestCaseRequestJson = objectMapper.writeValueAsString(submissionTestCaseCreateRequest);
            String problemDocumentUpdateAcceptRateRequestJson = objectMapper.writeValueAsString(problemDocumentUpdateAcceptRateRequest);
            log.info("submissionTestCaseRequestJson: {}", submissionTestCaseRequestJson);
            kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_UPDATE_ACCEPT_RATE_TOPIC, submissionTestCaseRequestJson, problemDocumentUpdateAcceptRateRequestJson);
            kafkaTemplate.send(KafkaConst.SUBMISSION_TEST_CASE_CREATE_TOPIC, submissionTestCaseRequestJson);
        } catch (Exception e) {
            log.error("Kafka message xử lý thất bại: {}", json, e);
        }
    }

    private static int getMaxCpuTime(List<SubmissionResponse> responses) {
        return (int) responses.stream()
                .filter(r -> r.getRun() != null)
                .mapToLong(r -> r.getRun().getCpuTime())
                .max()
                .orElse(0);
    }

    private static double getMaxMemory(List<SubmissionResponse> responses) {
        var tmp = -1.0;
        for (var item : responses) {
            if (item.getRun().getMemory() > tmp) {
                tmp = item.getRun().getMemory();
            }
        }
        log.info("max memory: {}", tmp);
        double value =  new BigDecimal(tmp / (1024 * 1024))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        log.info("max memory: {}", value);
        return value;
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
                        .pass(0).total(total).build();
            }

            if (run != null && "RE".equals(run.getStatus())) {
                return OverallResponse.builder()
                        .status(SubmissionStatus.RUNTIME_ERROR.getValue())
                        .message(run.getStderr())
                        .pass(0).total(total).build();
            }

            if (run != null && "TO".equals(run.getStatus())) {
                return OverallResponse.builder()
                        .status(SubmissionStatus.TIME_LIMIT_EXCEEDED.getValue())
                        .message(run.getStderr())
                        .pass(0).total(total).build();
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
        List<ProgrammingLanguage> pgs = new ArrayList<>();
        if (filterRequest.getProgrammingLanguageId() != null && !filterRequest.getProgrammingLanguageId().isEmpty()) {
            pgs = programmingLanguageRepo.findByProgrammingLanguageIds(filterRequest.getProgrammingLanguageId());
        }
        Specification<Submission> specification = SubmissionSpecification.hasProblemIdInRange(filterRequest.getProblemId())
                .and(SubmissionSpecification.hasSubmissionStatuses(filterRequest.getStatus()))
                .and(SubmissionSpecification.hasTimeRange(filterRequest.getTimeRange()))
                .and(SubmissionSpecification.hasProgrammingLanguages(pgs));

        Page<Submission> submissions = submissionRepo.findAll(specification, pagingSearch.toPageable());

        if (submissions.isEmpty()) {
            return null;
        }

        int page = submissions.getNumber() + 1;
        int totalPages = submissions.getTotalPages();

        List<BasicResultSubmissionResponse> basicResultSubmissionResponses = submissions.getContent().stream()
                .map(SubmissionMapper::toDto)
                .toList();
        return ListBasicResultSubmissionResponse.builder()
                .submissions(basicResultSubmissionResponses)
                .totalPages(totalPages)
                .page(page)
                .build();
    }

    @Override
    public ListBasicResultSubmissionResponse getUserSubmission(String userId, int problemId, Pageable pageable) {
        Page<Submission> submissions = submissionRepo.getSubmissionByUserIdAndProblemId(userId, problemId, pageable);
        List<BasicResultSubmissionResponse> basicResultSubmissionResponses = submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList();
        return ListBasicResultSubmissionResponse.builder()
                .page(submissions.getNumber()+1)
                .totalPages(submissions.getTotalPages())
                .submissions(basicResultSubmissionResponses)
                .build();
    }
}
