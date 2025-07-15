package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.ContestProblemRequest;

import java.util.List;

public interface ContestProblemService {
    void createContestProblem(int contestId, List<ContestProblemRequest> requests);
}
