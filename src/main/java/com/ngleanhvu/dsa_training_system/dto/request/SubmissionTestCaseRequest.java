package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubmissionTestCaseRequest {
    private int submissionId;
    private List<SubmissionTestCaseCreateRequest> submissionTestCaseCreateRequests;
}
