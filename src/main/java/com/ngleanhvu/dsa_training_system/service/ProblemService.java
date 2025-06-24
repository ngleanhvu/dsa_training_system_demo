package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;

public interface ProblemService {
    void createProblem(ProblemCreateRequest request);
}
