package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DiscussCreateRequest {
    private String userId;
    private String title;
    private String content;
    private List<Integer> tagIds;
}
