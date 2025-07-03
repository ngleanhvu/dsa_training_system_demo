package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemSearchRequest {
    private String title;
}
