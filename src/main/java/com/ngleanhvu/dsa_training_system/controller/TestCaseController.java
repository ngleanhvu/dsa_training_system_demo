package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
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
}
