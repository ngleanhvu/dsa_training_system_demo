package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ContestSubmissionCreateRequest {
    private Integer contestId;
    private Integer submissionId;
    private int score;
}
