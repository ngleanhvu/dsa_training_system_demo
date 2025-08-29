package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.TopicCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListTopicResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.TopicResponse;
import com.ngleanhvu.dsa_training_system.dto.response.TopicStatsResponse;

import java.util.List;

public interface TopicService {
    List<TopicResponse> getTopics() throws JsonProcessingException;
    void createTopic(TopicCreateRequest topicRequest);
    void updateTopic(Integer topicId, TopicCreateRequest topicRequest);
    void deleteTopic(Integer topicId);
    ListTopicResponse getAllTopics(String keyword, PagingSearch pagingSearch);
    List<TopicStatsResponse> statsTopic();
}
