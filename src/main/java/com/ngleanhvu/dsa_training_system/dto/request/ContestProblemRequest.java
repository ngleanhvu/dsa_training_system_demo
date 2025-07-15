package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class ContestProblemRequest {
    private int problemId;
    private int score;
    private int orderIndex;
}
