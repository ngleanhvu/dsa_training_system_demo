package com.ngleanhvu.dsa_training_system.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class ErrorResponse {
    private int code;
    private String message;
    private String details;
}
