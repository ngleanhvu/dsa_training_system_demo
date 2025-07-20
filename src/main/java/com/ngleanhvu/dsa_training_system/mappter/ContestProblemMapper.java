package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.ContestProblemResponse;
import com.ngleanhvu.dsa_training_system.entity.ContestProblem;

public class ContestProblemMapper {
    public static ContestProblemResponse toDto(ContestProblem entity) {
        return ContestProblemResponse.builder()
                .problemId(entity.getProblem().getProblemId())
                .title(entity.getProblem().getTitle())
                .score(entity.getScore())
                .build();
    }
}
