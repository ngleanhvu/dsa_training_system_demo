package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Top5ProblemSubmission {
    private int problemId;
    private String problemName;
    private int submissionCount;
}
