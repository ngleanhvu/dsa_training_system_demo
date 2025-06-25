package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.TestCaseMapper;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.repo.TestCaseRepo;
import com.ngleanhvu.dsa_training_system.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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


}
