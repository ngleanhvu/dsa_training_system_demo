package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseUpdateRequest {
    @NotNull(message = "Vui lòng nhập input")
    @NotEmpty(message = "Vui lòng nhập input")
    private String input;
    @NotNull(message = "Vui lòng nhập output")
    @NotEmpty(message = "Vui lòng nhập output")
    private String output;
}
