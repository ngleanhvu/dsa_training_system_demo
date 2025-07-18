package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.repo.CommentRepo;
import com.ngleanhvu.dsa_training_system.repo.DiscussRepo;
import com.ngleanhvu.dsa_training_system.repo.SolutionRepo;
import com.ngleanhvu.dsa_training_system.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final SolutionRepo solutionRepo;
    private final DiscussRepo discussRepo;
    private final CommentRepo commentRepo;

    @Override
    public Object[] byProblemSolvedAndUserId(String userId) {
        return new Object[0];
    }

    @Override
    public int bySolutionAndUserId(String userId) {
        return solutionRepo.countByUserId(userId);
    }

    @Override
    public int byDiscussAndUserId(String userId) {
        return discussRepo.countByUserId(userId);
    }

    @Override
    public Object[] byLanguageAndUserId(String userId) {
        return new Object[0];
    }

    @Override
    public Object[] bySkillAndUserId(String userId) {
        return new Object[0];
    }
}
