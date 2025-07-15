package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.ContestCreateRequest;

public interface ContestService {
    void createContest(ContestCreateRequest contest);
}
