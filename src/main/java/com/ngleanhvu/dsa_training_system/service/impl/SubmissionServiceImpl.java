package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListSubmissionResponse;
import com.ngleanhvu.dsa_training_system.dto.response.OverallResponse;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.entity.ProblemDetail;
import com.ngleanhvu.dsa_training_system.entity.ProgrammingLanguage;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ProblemDetailRepo;
import com.ngleanhvu.dsa_training_system.repo.ProgrammingLanguageRepo;
import com.ngleanhvu.dsa_training_system.repo.TestCaseRepo;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Value("${piston.load_balancer}")
    private String loadBalancer;

    @Value("${piston.port}")
    private int port;

    @Override
    public ListSubmissionResponse submit(SubmissionRequest submissionRequest) {
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

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

                    String URL = String.format("http://%s:%d/api/v2/execute", loadBalancer, port);

                    SubmissionResponse response;

                    try {
                        response = restTemplate.postForObject(URL, request, SubmissionResponse.class);
                        log.info("Submission response: {}", response);
                    } catch (Exception e) {
                        log.error("Error while submit: {}", e.getMessage());
                        return SubmissionResponse.builder()
                                .status(SubmissionStatus.ERROR.getValue())
                                .input(testCase.getInput())
                                .expectOutput(testCase.getOutput())
                                .build();
                    }

                    if (response == null) {
                        return SubmissionResponse.builder()
                                .status(SubmissionStatus.NULL_RESPONSE.getValue())
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

                    if (response.getRun() == null) {
                        response.setStatus(SubmissionStatus.ERROR.getValue());
                    } else if (response.getRun().getCode() != null && response.getRun().getCode() != 0) {
                        response.setStatus(SubmissionStatus.COMPILE_ERROR.getValue());
                    } else if (response.getRun().getStatus().equals("TO")) {
                        response.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED.getValue());
                    } else if (memoryUsed > memoryLimit) {
                        response.setStatus(SubmissionStatus.MEMORY_LIMIT_EXCEEDED.getValue());
                    } else if (!actualOutput.equals(expectedOutput)) {
                        response.setStatus(SubmissionStatus.WRONG_ANSWER.getValue());
                    } else {
                        response.setStatus(SubmissionStatus.ACCEPTED.getValue());
                    }


                    return response;
                }));
            }

            for (Future<SubmissionResponse> future : futures) {
                try {
                    allResponses.add(future.get(10, TimeUnit.SECONDS));
                } catch (TimeoutException e) {
                    SubmissionResponse timeoutResp = new SubmissionResponse();
                    timeoutResp.setStatus(SubmissionStatus.TIMEOUT.getValue());
                    allResponses.add(timeoutResp);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        double maxCpuTime = getMaxCpuTime(allResponses);
        log.info("Avg CPU Time: {} ms", maxCpuTime);

        double maxMemory = getMaxMemory(allResponses);
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


        log.info("List submission response: {}", listSubmissionResponse);

        return listSubmissionResponse;
    }


    private static double getMaxCpuTime(List<SubmissionResponse> responses) {
        return responses.stream()
                .filter(r -> r.getRun() != null)
                .mapToLong(r -> r.getRun().getCpuTime())
                .max()
                .orElse(0);
    }

    private static double getMaxMemory(List<SubmissionResponse> responses) {
        return responses.stream()
                .filter(r -> r.getRun() != null)
                .mapToLong(r -> r.getRun().getMemory())
                .max()
                .orElse(0);
    }

    private OverallResponse evaluateOverallStatus(List<SubmissionResponse> responses) {
        int total = responses.size();
        int pass = 0;

        for (SubmissionResponse r : responses) {
            String status = r.getStatus();
            var run = r.getRun();

            if (!SubmissionStatus.ACCEPTED.getValue().equals(status)) {
                if (run != null && run.getCode() != null && run.getCode() != 0) {
                    return OverallResponse.builder()
                            .status(SubmissionStatus.COMPILE_ERROR.getValue())
                            .message(run.getStderr())
                            .pass(0)
                            .total(0)
                            .build();
                }

                if (run != null && "RE".equals(run.getStatus())) {
                    return OverallResponse.builder()
                            .status(SubmissionStatus.RUNTIME_ERROR.getValue())
                            .message(run.getStderr())
                            .pass(0)
                            .total(0)
                            .build();
                }

                if (run != null && "TO".equals(run.getStatus())) {
                    return OverallResponse.builder()
                            .status(SubmissionStatus.TIME_LIMIT_EXCEEDED.getValue())
                            .message(run.getStderr())
                            .pass(0)
                            .total(0)
                            .build();
                }
            } else {
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

}
