package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SubmissionFilterRequest {
    private Integer problemId;
    private Integer programmingLanguageId;
    private RangeRequest<LocalDate> timeRange;
    private String status;
}
