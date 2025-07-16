package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.BasicResultSubmissionResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ListSubmissionResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;

import java.util.List;

public interface SubmissionService {
    ListSubmissionResponse submit(SubmissionRequest submissionRequest) throws JsonProcessingException;
    void createSubmission(String json) throws JsonProcessingException;
    List<BasicResultSubmissionResponse> getBasicSubmissionResponses(String userId, int problemId);
    List<BasicResultSubmissionResponse> getBasicSubmissionResponses(SubmissionFilterRequest filterRequest, PagingSearch pagingSearch);

}
