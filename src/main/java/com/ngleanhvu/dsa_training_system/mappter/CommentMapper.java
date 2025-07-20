package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.CommentResponse;
import com.ngleanhvu.dsa_training_system.entity.Comment;

public class CommentMapper {
    public static CommentResponse toDto(Comment comment) {
        return CommentResponse.builder()
                .commentCount(comment.getCommentCount())
                .upVotes(comment.getUpVotes())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userEmail(comment.getUser().getEmail())
                .userAvatar(comment.getUser().getAvatar())
                .userDisplayName(comment.getUser().getDisplayName())
                .build();
    }
}
