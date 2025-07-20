package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.entity.Discuss;

public class DiscussMapper {
    public static DiscussResponse toDto(Discuss s) {
        return com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse.builder()
                .title(s.getTitle())
                .content(s.getContent())
                .createdAt(s.getCreatedAt())
                .upVotes(s.getUpVotes())
                .views(s.getViews())
                .userAvatar(s.getUser().getAvatar())
                .userEmail(s.getUser().getEmail())
                .userDisplayName(s.getUser().getDisplayName())
                .build();
    }
}
