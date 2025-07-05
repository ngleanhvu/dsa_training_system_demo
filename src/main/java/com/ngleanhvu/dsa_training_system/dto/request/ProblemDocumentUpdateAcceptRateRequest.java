package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemDocumentUpdateAcceptRateRequest {
    private Integer problemId;
    private double acceptRate;
}
