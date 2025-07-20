package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProblemDocumentResponse {
    private Integer id;
    private String title;
    private double acceptanceRate;
    private Integer difficultyId;
    private String difficultyName;
    private List<Integer> topicIds;
    private LocalDate createdAt;
    private boolean isAccepted;
}
