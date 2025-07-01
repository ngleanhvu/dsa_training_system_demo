package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class SubmissionRequest {
    private Integer problemId;
    private String sourceCode;
    private Integer languageId;
    private String stdin;
}
