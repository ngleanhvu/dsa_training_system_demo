package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.DiscussDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.TagResponse;
import com.ngleanhvu.dsa_training_system.entity.Discuss;

import java.util.List;

public class DiscussMapper {
    public static DiscussResponse toDto(Discuss s) {
        return com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse.builder()
                .title(s.getTitle())
                .content(s.getContent())
                .comments(s.getCommentCount())
                .createdAt(s.getCreatedAt())
                .upVotes(s.getUpVotes())
                .views(s.getViews())
                .userAvatar(s.getUser().getAvatar())
                .userEmail(s.getUser().getEmail())
                .userDisplayName(s.getUser().getDisplayName())
                .discussId(s.getDiscussId())
                .build();
    }
    public static DiscussDetailResponse toDiscussDetailResponse(Discuss d) {
        return DiscussDetailResponse.builder()
                .discussId(d.getDiscussId())
                .title(d.getTitle())
                .content(d.getContent())
                .comments(d.getCommentCount())
                .views(d.getViews())
                .userEmail(d.getUser().getEmail())
                .userAvatar(d.getUser().getAvatar())
                .userDisplayName(d.getUser().getDisplayName())
                .createdAt(d.getCreatedAt())
                .upVotes(d.getUpVotes())
                .tags(d.getDiscussTags().stream()
                        .map(dt -> TagResponse.builder().tagId(dt.getTag().getTagId())
                                .name(dt.getTag().getName()).build())
                        .toList())
                .build();
    }
}
