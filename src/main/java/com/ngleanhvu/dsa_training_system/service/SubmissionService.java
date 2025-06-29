package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.entity.Submission;

public interface SubmissionService {
    String submitSubmission(SubmissionRequest submissionRequest);
    SubmissionResponse getSubmissionResult(String token);
}
