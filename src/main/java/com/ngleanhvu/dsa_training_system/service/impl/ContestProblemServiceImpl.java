package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.ContestProblemRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestProblemUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ContestProblemResponse;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.entity.ContestProblem;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ContestProblemRepo;
import com.ngleanhvu.dsa_training_system.repo.ContestRepo;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.service.ContestProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestProblemServiceImpl implements ContestProblemService {

    private final ContestProblemRepo contestProblemRepo;
    private final ContestRepo contestRepo;
    private final ProblemRepo problemRepo;

    @Transactional
    @Override
    public void createContestProblem(int contestId, List<ContestProblemRequest> requests) {
        Contest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", String.valueOf(contestId)));

        List<Integer> problemIds = requests.stream()
                .map(ContestProblemRequest::getProblemId).toList();

        List<Problem> problems = problemRepo.findAllById(problemIds);

        if (problems.size() != requests.size()) {
            Set<Integer> foundIds = problems.stream().map(Problem::getProblemId).collect(Collectors.toSet());
            List<Integer> missingIds = problemIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Problem(s)", "id(s)", missingIds.toString());
        }

        int problemCount = problems.size();

        List<ContestProblem> contestProblems = new ArrayList<>();

        for (int i = 0; i < problemCount; i++) {
            ContestProblem contestProblem = new ContestProblem();
            contestProblem.setContest(contest);
            contestProblem.setProblem(problems.get(i));
            contestProblem.setStatus(1);
            contestProblem.setScore(requests.get(i).getScore());
            contestProblem.setOrderIndex(requests.get(i).getOrderIndex());
            contestProblems.add(contestProblem);
        }

        contestProblemRepo.saveAll(contestProblems);
    }

    @Override
    @Transactional
    public void updateContestProblem(int contestId, List<ContestProblemUpdateRequest> requests) {
        Contest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", String.valueOf(contestId)));

        if (requests == null || requests.isEmpty()) {
            return;
        }

        Set<Integer> newProblemIds = requests.stream()
                .map(ContestProblemUpdateRequest::getProblemId)
                .collect(Collectors.toSet());

        Map<Integer, Integer> problemIdToScore = requests.stream()
                .collect(Collectors.toMap(ContestProblemUpdateRequest::getProblemId, ContestProblemUpdateRequest::getScore));

        List<ContestProblem> existingContestProblems = contestProblemRepo.findByContestId(contestId);

        Set<Integer> currentProblemIds = existingContestProblems.stream()
                .map(cp -> cp.getProblem().getProblemId())
                .collect(Collectors.toSet());

        Set<Integer> toRemove = new HashSet<>(currentProblemIds);
        toRemove.removeAll(newProblemIds);

        if (!toRemove.isEmpty()) {
            contestProblemRepo.deleteByContestIdAndProblemId(contestId, toRemove);
        }

        for (ContestProblem cp : existingContestProblems) {
            int pid = cp.getProblem().getProblemId();
            if (newProblemIds.contains(pid)) {
                Integer newScore = problemIdToScore.get(pid);
                if (newScore != null && !Objects.equals(cp.getScore(), newScore)) {
                    cp.setScore(newScore);
                }
            }
        }

        Set<Integer> toAdd = new HashSet<>(newProblemIds);
        toAdd.removeAll(currentProblemIds);

        if (!toAdd.isEmpty()) {
            List<Problem> problemsToAdd = problemRepo.findAllById(toAdd);

            List<ContestProblem> contestProblemsToAdd = problemsToAdd.stream()
                    .map(problem -> ContestProblem.builder()
                            .contest(contest)
                            .problem(problem)
                            .score(problemIdToScore.get(problem.getProblemId()))
                            .status(1)
                            .build())
                    .toList();

            contestProblemRepo.saveAll(contestProblemsToAdd);
        }

        contestProblemRepo.saveAll(existingContestProblems);
    }


    @Override
    public List<ContestProblemResponse> getContestProblemsByContestId(int contestId) {

        List<ContestProblem> contestProblems = contestProblemRepo.findByContestId(contestId);

        if (contestProblems.isEmpty()) {
            return Collections.emptyList();
        }

        List<ContestProblemResponse> contestProblemResponses = contestProblems.stream()
                .map(c -> ContestProblemResponse.builder()
                        .problemId(c.getProblem().getProblemId())
                        .title(c.getProblem().getTitle())
                        .score(c.getScore())
                        .build())
                .toList();

        log.debug("contestProblemResponses: {}", contestProblemResponses);

        return contestProblemResponses;
    }
}
