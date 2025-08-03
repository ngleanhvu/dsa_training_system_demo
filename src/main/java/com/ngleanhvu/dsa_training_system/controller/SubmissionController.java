package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    private final JwtUtil jwtUtil;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmissionRequest submissionRequest) throws JsonProcessingException {
        var submissionResponse = submissionService.submit(submissionRequest);

        ApiResponse<?> response = ApiResponse.builder()
                .message("Submit test cases success")
                .status(HttpStatus.OK.name())
                .metadata(submissionResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/problems/{problemId}/users")
    public ResponseEntity<?> getSubmissionsForUserAndProblems(@PathVariable("problemId") int problemId,
                                                              @RequestHeader("Authorization") String token)  {

        String userId = String.valueOf(jwtUtil.getSubject(token));
        var response = submissionService.getBasicSubmissionResponses(userId, problemId);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get submissions success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> getSubmissionsForAdmin(@RequestBody SubmissionFilterRequest submissionFilterRequest,
                                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(required = false, defaultValue = "1") int page,
                                                    @RequestParam(required = false, defaultValue = "5") int size,
                                                    @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setPage(Math.max(0, page - 1));
        pagingSearch.setSize(size);
        pagingSearch.setDirection(sortDir);
        var responses = submissionService.getBasicSubmissionResponses(submissionFilterRequest, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .message("Get submissions success")
                .status(HttpStatus.OK.name())
                .metadata(responses)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
