package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListProblemResponse {
    private List<ProblemResponse> problems;
    private int page;
    private int totalPage;
    private int totalElements;
}
