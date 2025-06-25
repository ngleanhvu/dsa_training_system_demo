package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.TestCase;

import java.util.List;

public interface TestCaseService {
    TestCase createTestCase(TestCaseCreateRequest request, int problemId);
    void createTestCases(List<TestCaseCreateRequest> requests, int problemId);
}
