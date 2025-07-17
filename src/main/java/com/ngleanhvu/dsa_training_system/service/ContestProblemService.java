package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.ContestProblemRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestProblemUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ContestProblemResponse;

import java.util.List;

public interface ContestProblemService {
    void createContestProblem(int contestId, List<ContestProblemRequest> requests);
    void updateContestProblem(int contestId, List<ContestProblemUpdateRequest> requests);
    List<ContestProblemResponse> getContestProblemsByContestId(int contestId);
}
