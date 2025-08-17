package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.response.*;

import java.util.List;

public interface StatisticService {
    CommonStatisticForAdmin getCommonStatisticForAdmin();
    List<Top5ProblemSubmission> getTop5ProblemSubmission();
    List<DifficultStatsResponse>  getDifficultStatsResponse();
    List<SubmissionStatsEachYear> getSubmissionStatsEachYear(int year);
    List<UserStatsEachYear> getUserStatsEachYear(int year);
    List<DifficultUserResponse> getDifficultUserResponse(String email);
    ProblemUserSolved getProblemUserSolved(String email);
    List<SubmissionCountResponse>  getSubmissionCountResponse(String email, Integer year);
}
