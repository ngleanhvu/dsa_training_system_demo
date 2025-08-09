package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    @NotNull(message = "Vui lòng nhập nội dung")
    private String content;
    private Integer parentCommentId;
    private String userId;
}
