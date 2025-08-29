package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.TestCaseUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.TestCaseService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDirection, Math.max(page-1,0), pageSize);
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
                                            @Valid @RequestBody TestCaseUpdateRequest testCaseUpdateRequest) {
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{testCaseId}")
    public ResponseEntity<?> getTestCaseById(@PathVariable("testCaseId") Integer testCaseId) {
        var response = testCaseService.getTestCaseById(testCaseId);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get test case success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getTestCases(@RequestParam(required = false, defaultValue = "0") Integer problemId,
                                          @RequestParam(required = false, defaultValue = "testCaseId") String sortBy,
                                          @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDirection, Math.max(page-1,0), pageSize);
        var response = testCaseService.getAllTestCases(problemId, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get test cases success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/{problemId}")
    public ResponseEntity<?> uploadTestCase(@RequestPart("file") MultipartFile file,
                                            @PathVariable("problemId") Integer problemId) throws IOException {
        testCaseService.uploadTestCase(problemId, file);
        var response = ApiResponse.builder()
                .message("Uploaded test case success")
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/generate")
    public void generateFile(
            @RequestParam int n,
            @RequestParam long min,
            @RequestParam long max,
            @RequestParam(defaultValue = "true") boolean allowDuplicate,
            HttpServletResponse response
    ) throws Exception {

        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        if (!allowDuplicate && n > (max - min + 1)) {
            throw new IllegalArgumentException("n is larger than available unique numbers");
        }

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"array.txt\"");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            if (allowDuplicate) {
                for (int i = 0; i < n; i++) {
                    long value = ThreadLocalRandom.current().nextLong(min, max + 1);
                    writer.write(Long.toString(value));
                    if (i < n - 1) writer.write(" ");
                }
            } else {
                java.util.Set<Long> set = new java.util.HashSet<>();
                while (set.size() < n) {
                    long value = ThreadLocalRandom.current().nextLong(min, max + 1);
                    if (set.add(value)) {
                        writer.write(Long.toString(value));
                        if (set.size() < n) writer.write(" ");
                    }
                }
            }
        }
    }
}
