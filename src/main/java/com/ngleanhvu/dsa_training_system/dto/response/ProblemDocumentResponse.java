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
    private String url;
    private String acceptanceRate;
    private Integer difficultyId;
    private String difficultyName;
    private List<Integer> topicIds;
    private List<String> topicNames;
    private LocalDate createdAt;
    private boolean isPublic;
    private boolean isAccepted;
}
