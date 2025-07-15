package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.TestCaseUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.TestCaseResponse;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.TestCaseMapper;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.repo.TestCaseRepo;
import com.ngleanhvu.dsa_training_system.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

    private final TestCaseRepo testCaseRepo;
    private final ProblemRepo problemRepo;

    @Transactional
    @Override
    public TestCase createTestCase(TestCaseCreateRequest request, int problemId) {

        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        log.info("Test case: {}", request);

        return TestCase.builder()
                .input(request.getInput().trim())
                .output(request.getOutput().trim())
                .problem(problem)
                .status(1)
                .build();
    }

    @Transactional
    @Override
    public void createTestCases(List<TestCaseCreateRequest> requests, int problemId) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        List<TestCase> testCases = requests.stream()
                .map(t -> TestCaseMapper.mapToTestCase(t, problem))
                .toList();

        testCaseRepo.saveAll(testCases);
    }

    @Override
    public List<TestCaseResponse> getTestCaseByProblemId(int problemId, PagingSearch pagingSearch) {
        Page<TestCase> testCases = testCaseRepo.findAllByProblemId(problemId, pagingSearch.toPageable());

        List<TestCase> testCaseList = testCases.getContent();

        log.info("Test cases: {}", testCaseList);

        List<TestCaseResponse> testCaseResponses = testCaseList.stream()
                .map(t -> TestCaseResponse.builder()
                        .testCaseId(t.getTestCaseId())
                        .input(t.getInput())
                        .output(t.getOutput())
                        .problemId(t.getProblem().getProblemId())
                        .build())
                .toList();

        log.info("Test caseResponses: {}", testCaseResponses);

        return testCaseResponses;
    }

    @Override
    public void updateTestCase(Integer testCaseId, TestCaseUpdateRequest request) {
        TestCase existingTestCase = testCaseRepo.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", String.valueOf(testCaseId)));

        existingTestCase.setInput(request.getInput() == null ? existingTestCase.getInput() : request.getInput().trim());
        existingTestCase.setOutput(request.getOutput() == null ? existingTestCase.getOutput() : request.getOutput().trim());

        testCaseRepo.save(existingTestCase);
    }

    @Override
    public void deleteAllTestCaseByProblemId(int problemId) {
        List<TestCase> testCases = testCaseRepo.findAllByProblemId(problemId);
        testCaseRepo.deleteAll(testCases);
    }

    @Override
    public void deleteTestCaseById(int testCaseId) {
        testCaseRepo.deleteById(testCaseId);
    }


}
