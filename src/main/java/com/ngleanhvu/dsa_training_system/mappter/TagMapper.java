package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.TagResponse;
import com.ngleanhvu.dsa_training_system.entity.Tag;

public class TagMapper {
    public static TagResponse mapResponse(Tag tag) {
        return TagResponse.builder()
                .tagId(tag.getTagId())
                .name(tag.getName())
                .build();
    }
}
