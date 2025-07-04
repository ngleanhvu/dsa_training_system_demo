package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

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
}
