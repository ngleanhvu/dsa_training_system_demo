package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class ContestCreateRequest {
    private String title;
    private String description;
    private String startTime;
    private int durationMinutes;
}
