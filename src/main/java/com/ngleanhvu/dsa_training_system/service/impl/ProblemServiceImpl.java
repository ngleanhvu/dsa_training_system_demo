package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Difficulty;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.repo.ProblemDetailRepo;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepo problemRepo;
    private final ProblemDetailRepo problemDetailRepo;
    private

    @Override
    public void createProblem(ProblemCreateRequest request) {
        Difficulty difficulty =

        Problem problem = Problem.builder()
                .title(request.getTitle())
                .difficulty()
    }
}
