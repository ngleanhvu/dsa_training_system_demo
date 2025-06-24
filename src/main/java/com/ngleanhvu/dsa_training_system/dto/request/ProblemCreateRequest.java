package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProblemCreateRequest {
    private String title;
    private int difficultId;
    private List<Integer> topicIds;
    private String description;
    private List<String> constraints;
    private List<String> hints;
    private int timeLimit;
    private int memoryLimit;
}
