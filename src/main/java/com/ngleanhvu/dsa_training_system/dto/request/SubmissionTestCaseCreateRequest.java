package com.ngleanhvu.dsa_training_system.dto.request;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionTestCaseCreateRequest {
    private int testCaseId;
    private int memory;
    private int runtime;
    private SubmissionStatus status;
}
