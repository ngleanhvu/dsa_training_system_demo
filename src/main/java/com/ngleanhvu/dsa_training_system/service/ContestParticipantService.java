package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.response.ContestParticipantLeaderboard;

import java.util.List;

public interface ContestParticipantService {
    void enroll(String userId, int contestId);
    List<ContestParticipantLeaderboard> getContestParticipantLeaderboard(Integer contestId);
}
