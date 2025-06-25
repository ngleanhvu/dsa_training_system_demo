package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseCreateRequest {
    @NotNull
    private String input;
    @NotNull
    private String output;
}
