package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.TestCaseUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.TestCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/test-cases")
public class TestCaseController {

    private final TestCaseService testCaseService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/problems/{problemId}")
    public ResponseEntity<?> createTestCases(@Valid @RequestBody List<TestCaseCreateRequest> requests,
                                             @PathVariable("problemId") int problemId) {
        testCaseService.createTestCases(requests, problemId);
        var response = ApiResponse.builder()
                .message("Created test cases success")
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/problems/{problemId}")
    public ResponseEntity<?> getTestCasesByProblem(@PathVariable("problemId") int problemId,
                                                   @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                                   @RequestParam(required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setPage(page);
        pagingSearch.setDirection(sortDirection);
        pagingSearch.setSize(pageSize);
        var response = testCaseService.getTestCaseByProblemId(problemId, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get test cases success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{testCaseId}")
    public ResponseEntity<?> updateTestCase(@PathVariable("testCaseId") Integer testCaseId,
                                            @RequestBody TestCaseUpdateRequest testCaseUpdateRequest) {
        testCaseService.updateTestCase(testCaseId, testCaseUpdateRequest);
        var response = ApiResponse.builder()
                .message("Updated test case success")
                .status(HttpStatus.OK.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/problems/{problemId}")
    public ResponseEntity<?> deleteTestCasesByProblem(@PathVariable("problemId") Integer problemId) {
        testCaseService.deleteAllTestCaseByProblemId(problemId);
        var response = ApiResponse.builder()
                .message("Deleted test cases success")
                .status(HttpStatus.NO_CONTENT.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{testCaseId}")
    public ResponseEntity<?> deleteTestCase(@PathVariable("testCaseId") Integer testCaseId) {
        testCaseService.deleteTestCaseById(testCaseId);
        var response = ApiResponse.builder()
                .message("Deleted test case success")
                .status(HttpStatus.NO_CONTENT.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
