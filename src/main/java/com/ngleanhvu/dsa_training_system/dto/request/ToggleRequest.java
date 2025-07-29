package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class ToggleRequest {
    private Integer problemId;
    private boolean isPublic;
}
