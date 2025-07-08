package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class CommentResponse {
    private String content;
    private int upVotes;
    private int downVotes;
    private int views;
    private int commentCount;
    private LocalDateTime createdAt;

    private String userEmail;
    private String userDisplayName;
    private String userAvatar;
}
