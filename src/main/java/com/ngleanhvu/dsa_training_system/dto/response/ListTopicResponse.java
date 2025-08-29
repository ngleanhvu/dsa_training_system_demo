package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListTopicResponse {
    private List<TopicResponse> topics;
    private int page;
    private int totalPages;
}
