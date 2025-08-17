package com.ngleanhvu.dsa_training_system.dto.response;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistorySubmissionProblemResponse {
    private SubmissionStatus status;
    private Integer pass;
    private Integer total;
    private String sourceCode;
    private String message;
}
