package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentUpdateRequest {
    @NotNull(message = "Vui lòng nhập nội ")
    private String content;
    private String userId;
}
