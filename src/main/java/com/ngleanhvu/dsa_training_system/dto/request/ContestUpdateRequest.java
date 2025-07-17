package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class ContestUpdateRequest {
    private String title;
    private String description;
    private String startTime;
    private int durationMinutes;
    private String status;
    private String endTime;
}
