package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DiscussForUpdateResponse {
    private String title;
    private String content;
    private List<Integer> tagIds;
}
