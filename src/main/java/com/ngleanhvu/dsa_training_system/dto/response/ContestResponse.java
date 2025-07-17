package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContestResponse {
    private Integer contestId;
    private String title;
    private LocalDateTime startTime;
}
