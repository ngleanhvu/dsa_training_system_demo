package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicStatsResponse {
    private String name;
    private long count;
}
