package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemUserSolved {
    private Long solved;
    private Long total;
}
