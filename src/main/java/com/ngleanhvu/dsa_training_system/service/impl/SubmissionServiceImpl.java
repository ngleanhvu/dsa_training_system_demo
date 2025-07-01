package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.entity.ProblemDetail;
import com.ngleanhvu.dsa_training_system.entity.ProgrammingLanguage;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ProblemDetailRepo;
import com.ngleanhvu.dsa_training_system.repo.ProgrammingLanguageRepo;
import com.ngleanhvu.dsa_training_system.repo.TestCaseRepo;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final ProgrammingLanguageRepo programmingLanguageRepo;
    private final ProblemDetailRepo problemDetailRepo;
    private final RestTemplate restTemplate;
    private final TestCaseRepo testCaseRepo;

    @Override
    public List<SubmissionResponse> submit(SubmissionRequest submissionRequest) {
        ProgrammingLanguage programmingLanguage = programmingLanguageRepo.findById(submissionRequest.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", String.valueOf(submissionRequest.getLanguageId())));

        ProblemDetail problemDetail = problemDetailRepo.findByProblemId(submissionRequest.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("ProblemDetail", "id", String.valueOf(submissionRequest.getProblemId())));

        List<TestCase> testCases = testCaseRepo.findAllByProblemId(submissionRequest.getProblemId());

        if (testCases.isEmpty()) {
            throw new InvalidValueException("Test case quantity must be more than 1");
        }

        List<SubmissionResponse> allResponses = new ArrayList<>();

        // Dùng try-with-resources để đảm bảo executor shutdown đúng
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<SubmissionResponse>> futures = new ArrayList<>();

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

                    String URL = "http://13.251.81.69:2000/api/v2/execute";
                    SubmissionResponse response = restTemplate.postForObject(URL, request, SubmissionResponse.class);

                    if (response == null) {
                        throw new RuntimeException("Null response from code execution API");
                    }

                    response.setInput(testCase.getInput());
                    response.setExpectOutput(testCase.getOutput());

                    return response;
                }));
            }

            // Lấy kết quả từng thread
            for (Future<SubmissionResponse> future : futures) {
                try {
                    // future.get() có thể throw nên bọc kỹ
                    allResponses.add(future.get(10, TimeUnit.SECONDS)); // tránh chờ mãi
                } catch (TimeoutException e) {
                    // Nếu bị timeout thì có thể tạo một response lỗi đặc biệt
                    SubmissionResponse timeoutResp = new SubmissionResponse();
                    timeoutResp.setStatus("TIMEOUT");
                    allResponses.add(timeoutResp);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        return allResponses;
    }

}
