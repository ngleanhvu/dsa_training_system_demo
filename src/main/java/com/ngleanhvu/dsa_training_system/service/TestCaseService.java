package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.TestCaseUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListTestCaseResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.TestCaseResponse;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TestCaseService {
    TestCase createTestCase(TestCaseCreateRequest request, int problemId);
    void createTestCases(List<TestCaseCreateRequest> requests, int problemId);
    ListTestCaseResponse getTestCaseByProblemId(int problemId, PagingSearch pagingSearch);
    void updateTestCase(Integer testCaseId, TestCaseUpdateRequest request);
    void deleteAllTestCaseByProblemId(int problemId);
    void deleteTestCaseById(int testCaseId);
    TestCaseResponse getTestCaseById(int testCaseId);
    ListTestCaseResponse getAllTestCases(Integer problemId, PagingSearch pagingSearch);
    void uploadTestCase(Integer problemId, MultipartFile file) throws IOException;
}
