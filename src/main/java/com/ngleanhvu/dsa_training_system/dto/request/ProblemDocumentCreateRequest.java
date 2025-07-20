package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProblemDocumentCreateRequest {
    private Integer problemId;
    private String title;
    private Integer difficultyId;
    private String url;
    private String difficultyName;
    private List<Integer> topicIds;
    private double acceptanceRate;
    private LocalDate createdAt;
}
