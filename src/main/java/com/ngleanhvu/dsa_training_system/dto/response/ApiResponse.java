package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T>   {
    private String message;
    private String status;
    private T metadata;

    public static ApiResponse<?> create(String message, String status) {
        return ApiResponse.builder().status(status).message(message).build();
    }
}
