package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Data;

@Data
public class SubmissionResponse {
    private String stdout;
    private String stderr;
    private String compile_output;
    private int statusId;
    private String statusDescription;
}
