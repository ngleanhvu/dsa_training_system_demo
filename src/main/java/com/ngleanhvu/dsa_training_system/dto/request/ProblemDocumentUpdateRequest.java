package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.util.Set;


@Data
public class ProblemDocumentUpdateRequest {
    private Integer problemId;
    private String title;
    private String difficultyName;
    private Integer difficultyId;
    private Set<Integer> topicIds;
}
