package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProblemDetailResponse {
    private Integer problemId;
    private String title;
    private String description;
    private List<String> hints;
    private String constraints;
    private int memoryLimit;
    private int timeLimit;
    private List<TopicResponse> topics;
    private List<ExampleResponse> examples;
    private DifficultResponse difficult;
}
