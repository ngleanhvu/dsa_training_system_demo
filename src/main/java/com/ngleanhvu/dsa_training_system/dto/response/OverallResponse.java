package com.ngleanhvu.dsa_training_system.dto.response;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverallResponse {
    private String status;
    private String message;
    private int pass;
    private int total;
}
