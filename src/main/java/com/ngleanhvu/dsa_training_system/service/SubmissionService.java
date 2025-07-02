package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListSubmissionResponse;

public interface SubmissionService {
    ListSubmissionResponse submit(SubmissionRequest submissionRequest);
}
