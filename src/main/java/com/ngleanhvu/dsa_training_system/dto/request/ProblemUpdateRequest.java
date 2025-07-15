package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProblemUpdateRequest {
    private String title;
    private Integer difficultId;
    private List<Integer> topicIds;
    private String description;
    private List<String> constraints;
    private List<String> hints;
    private Integer timeLimit;
    private Integer memoryLimit;
}
