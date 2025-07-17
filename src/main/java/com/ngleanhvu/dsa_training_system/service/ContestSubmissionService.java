package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.entity.ContestSubmission;

public interface ContestSubmissionService {
    void createContestSubmission(String json) throws JsonProcessingException;
}
