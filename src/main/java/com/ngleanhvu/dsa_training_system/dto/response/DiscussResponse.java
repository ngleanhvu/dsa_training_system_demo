package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DiscussResponse {
    private String title;
    private String content;
    private int upVotes;
    private int downVotes;
    private int comments;
    private int views;
    private LocalDateTime createdAt;

    private String userEmail;
    private String userAvatar;
    private String userDisplayName;
}
