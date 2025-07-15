package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.TestCaseUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.TestCaseResponse;
import com.ngleanhvu.dsa_training_system.entity.TestCase;

import java.util.List;

public interface TestCaseService {
    TestCase createTestCase(TestCaseCreateRequest request, int problemId);
    void createTestCases(List<TestCaseCreateRequest> requests, int problemId);
    List<TestCaseResponse> getTestCaseByProblemId(int problemId, PagingSearch pagingSearch);
    void updateTestCase(Integer testCaseId, TestCaseUpdateRequest request);
    void deleteAllTestCaseByProblemId(int problemId);
    void deleteTestCaseById(int testCaseId);
}
