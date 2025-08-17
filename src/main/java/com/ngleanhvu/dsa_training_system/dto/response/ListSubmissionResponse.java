package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListSubmissionResponse {
    private String submissionId;
    private String sourcecode;
    private String language;
    private OverallResponse status;
    private double runtime;
    private double memory;
    private String message;
    private List<SubmissionResponse> submissionResponses;
}
