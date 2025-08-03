package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {
    private Integer tagId;
    private String name;
}
