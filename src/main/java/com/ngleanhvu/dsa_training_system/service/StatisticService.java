package com.ngleanhvu.dsa_training_system.service;

public interface StatisticService {
    Object[] byProblemSolvedAndUserId(String userId);
    int bySolutionAndUserId(String userId);
    int byDiscussAndUserId(String userId);
    Object[] byLanguageAndUserId(String userId);
    Object[] bySkillAndUserId(String userId);
}
