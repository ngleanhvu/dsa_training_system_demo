package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.ContestProblemRequest;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.entity.ContestProblem;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ContestProblemRepo;
import com.ngleanhvu.dsa_training_system.repo.ContestRepo;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.service.ContestProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
}
