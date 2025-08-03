package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.response.*;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final UserRepo userRepo;
    private final ProblemRepo problemRepo;
    private final DiscussRepo discussRepo;
    private final ContestRepo contestRepo;
    private final SubmissionRepo submissionRepo;

    // ADMIN
    @Override
    public CommonStatisticForAdmin getCommonStatisticForAdmin() {
        int totalUsers = (int) userRepo.count();
        int totalProblems = (int) problemRepo.count();
        int totalDiscusses = (int) discussRepo.count();
        int totalContests = (int) contestRepo.count();

        CommonStatisticForAdmin commonStatisticForAdmin = new CommonStatisticForAdmin();
        commonStatisticForAdmin.setTotalUsers(totalUsers);
        commonStatisticForAdmin.setTotalProblems(totalProblems);
        commonStatisticForAdmin.setTotalDiscusses(totalDiscusses);
        commonStatisticForAdmin.setTotalContests(totalContests);
        return commonStatisticForAdmin;
    }

    // ADMIN
    @Override
    public List<Top5ProblemSubmission> getTop5ProblemSubmission() {
        List<Object[]> result = problemRepo.getTop5ProblemsSubmissions();
        log.info("getTop5ProblemSubmission raw result: {}", result);

        List<Top5ProblemSubmission> responses = result.stream()
                .map(item -> Top5ProblemSubmission.builder()
                        .problemId(((Number) item[0]).intValue())
                        .problemName((String) item[1])
                        .submissionCount(((Number) item[2]).intValue())
                        .build())
                .toList();

        log.info("getTop5ProblemSubmission mapped result: {}", responses);
        return responses;
    }


    // ADMIN
    @Override
    public List<DifficultStatsResponse> getDifficultStatsResponse() {
        List<Object[]> result = problemRepo.getDifficultyStats();
        log.info("getDifficultStatsResponse: {}", result);
        List<DifficultStatsResponse> responses = result.stream()
                .map(item -> DifficultStatsResponse.builder()
                        .difficultName((String) item[0])
                        .totalProblems(((Number) item[1]).intValue())
                        .build())
                .toList();
        log.info("getDifficultStatsResponse: {}", responses);
        return responses;
    }

    // ADMIN
    @Override
    public List<SubmissionStatsEachYear> getSubmissionStatsEachYear(int year) {
        List<Object[]> result = submissionRepo.getSubmissionByEachYear(year);
        log.info("getSubmissionStatsEachYear: {}", result);
        List<SubmissionStatsEachYear> responses = result.stream()
                .map(item -> SubmissionStatsEachYear.builder()
                        .month(((Number) item[0]).intValue())
                        .quantity(((Number) item[1]).intValue())
                        .build())
                .toList();
        log.info("getSubmissionStatsEachYear: {}", responses);
        return responses;
    }

    //
    @Override
    public List<UserStatsEachYear> getUserStatsEachYear(int year) {
        List<Object[]> result = userRepo.getUserByEachYear(year);
        log.info("getUserStatsEachYear: {}", result);
        List<UserStatsEachYear> responses = result.stream()
                .map(item -> UserStatsEachYear.builder()
                        .month(((Number) item[0]).intValue())
                        .quantity(((Number) item[1]).intValue())
                        .build())
                .toList();
        log.info("getUserStatsEachYear: {}", responses);
        return responses;
    }


}
