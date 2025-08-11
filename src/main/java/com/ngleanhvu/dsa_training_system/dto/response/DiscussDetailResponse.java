package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DiscussDetailResponse {
    private Integer discussId;
    private String title;
    private String content;
    private int upVotes;
    private int downVotes;
    private int comments;
    private int views;
    private LocalDateTime createdAt;
    private List<TagResponse> tags;
    private Long isUpVote = 0L;

    private String userEmail;
    private String userAvatar;
    private String userDisplayName;
}
