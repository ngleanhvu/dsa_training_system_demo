package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.response.TopicResponse;

import java.util.List;

public interface TopicService {
    List<TopicResponse> getTopics() throws JsonProcessingException;
}
