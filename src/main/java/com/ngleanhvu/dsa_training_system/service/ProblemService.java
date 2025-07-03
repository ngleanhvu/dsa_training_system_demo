package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;

public interface ProblemService {
    void createProblem(ProblemCreateRequest request) throws JsonProcessingException;

}
