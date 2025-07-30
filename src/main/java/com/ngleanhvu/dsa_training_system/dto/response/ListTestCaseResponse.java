package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListTestCaseResponse {
    private int page;
    private int totalPages;
    private List<TestCaseResponse> testCases;
}
