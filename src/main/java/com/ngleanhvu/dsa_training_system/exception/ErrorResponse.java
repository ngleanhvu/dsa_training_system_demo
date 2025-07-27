package com.ngleanhvu.dsa_training_system.exception;

import com.ngleanhvu.dsa_training_system.exception.FieldErrorDetail;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
    private List<FieldErrorDetail> errors;
}