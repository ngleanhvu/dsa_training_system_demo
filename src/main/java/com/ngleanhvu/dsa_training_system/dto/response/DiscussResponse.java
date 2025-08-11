package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussResponse {
    private Integer discussId;
    private String title;
    private String content;
    private int upVotes;
    private int downVotes;
    private int comments;
    private int views;
    private LocalDateTime createdAt;
    private Long isUpVote = 0L;

    private String userEmail;
    private String userAvatar;
    private String userDisplayName;
}
