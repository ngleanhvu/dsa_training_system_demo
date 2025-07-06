package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.entity.TestCase;

public class TestCaseMapper {
    public static TestCase mapToTestCase(TestCaseCreateRequest request, Problem problem) {
        return TestCase.builder()
                .input(request.getInput())
                .output(request.getOutput())
                .status(1)
                .problem(problem)
                .build();
    }
}
