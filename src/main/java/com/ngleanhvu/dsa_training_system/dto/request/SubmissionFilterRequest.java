package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SubmissionFilterRequest {
    private RangeRequest<Integer> problemId;
    private List<Integer> programmingLanguageId;
    private RangeRequest<LocalDate> timeRange;
    private List<String> status;
}
