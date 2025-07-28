package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicResponse {
    private Integer topicId;
    private String topicName;
}
