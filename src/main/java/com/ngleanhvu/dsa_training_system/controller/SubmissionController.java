package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("/submit")
    public String submitCode(@RequestBody SubmissionRequest request) {
        return submissionService.submitSubmission(request);
    }

    @GetMapping("/result/{token}")
    public SubmissionResponse getResult(@PathVariable String token) {
        return submissionService.getSubmissionResult(token);
    }
}
