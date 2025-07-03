package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.entity.SubmissionTestCase;

import java.util.List;

public interface SubmissionTestCaseService {
    void createSubmissionTestCase(String json) throws JsonProcessingException;
}
