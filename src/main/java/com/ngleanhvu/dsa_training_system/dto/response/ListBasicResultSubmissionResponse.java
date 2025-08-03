package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListBasicResultSubmissionResponse {
    private List<BasicResultSubmissionResponse> submissions;
    private int page;
    private int totalPages;
}
