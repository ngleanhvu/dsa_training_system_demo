package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListSubmissionResponse;

public interface SubmissionService {
    ListSubmissionResponse submit(SubmissionRequest submissionRequest) throws JsonProcessingException;
    void createSubmission(String json) throws JsonProcessingException;
}
