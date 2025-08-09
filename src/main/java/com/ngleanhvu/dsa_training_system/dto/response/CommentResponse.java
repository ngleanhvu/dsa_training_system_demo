package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Integer commentId;
    private String content;
    private Integer upVotes;
    private Integer downVotes;
    private Integer views;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private Long isUpVote = 0L;

    private String userEmail;
    private String userDisplayName;
    private String userAvatar;
}
