package com.ngleanhvu.dsa_training_system.dto.request;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SubmissionCreateRequest {
    private String userId;
    private int pass;
    private int total;
    private String message;
    private int problemId;
    private int programmingLanguageId;
    private String sourceCode;
    private double memory;
    private double runtime;
    private LocalDateTime submitTime;
    private String status;
    private List<SubmissionTestCaseCreateRequest> submissionTestCaseCreateRequests;
    private Integer contestId;
}
