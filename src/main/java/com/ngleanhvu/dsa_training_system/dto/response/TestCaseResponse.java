package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestCaseResponse {
    private String input;
    private String output;
    private Integer problemId;
    private Integer testCaseId;
}
