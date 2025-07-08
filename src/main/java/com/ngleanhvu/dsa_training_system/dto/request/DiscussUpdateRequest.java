package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DiscussUpdateRequest {
    private String title;
    private String content;
    private List<Integer> tagIds;
}
