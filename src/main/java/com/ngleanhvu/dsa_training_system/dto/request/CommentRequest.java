package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Integer parentCommentId;
    private String userId;
}
