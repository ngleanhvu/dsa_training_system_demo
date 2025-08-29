package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.request.TopicCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListTopicResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.TopicResponse;
import com.ngleanhvu.dsa_training_system.dto.response.TopicStatsResponse;
import com.ngleanhvu.dsa_training_system.entity.Topic;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.redis.RedisKey;
import com.ngleanhvu.dsa_training_system.repo.TopicRepo;
import com.ngleanhvu.dsa_training_system.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final TopicRepo topicRepo;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<TopicResponse> getTopics() throws JsonProcessingException {
        List<TopicResponse> topicResponses = getTopicsFromRedis();
        if (topicResponses == null) {
            topicResponses = topicRepo.findAll().stream()
                    .map(t -> TopicResponse.builder()
                            .topicId(t.getTopicId())
                            .topicName(t.getName())
                            .build())
                    .toList();
            String key = RedisKey.generateTopicKey();
            String value = objectMapper.writeValueAsString(topicResponses);
            stringRedisTemplate.opsForValue().set(key, value);
        }
        return topicResponses;
    }

    @Transactional
    @Override
    public void createTopic(TopicCreateRequest topicRequest) {
        Topic topic = Topic.builder()
                .name(topicRequest.getName())
                .build();
        topicRepo.save(topic);
        String key = RedisKey.generateTopicKey();
        stringRedisTemplate.delete(key);
    }

    @Transactional
    @Override
    public void updateTopic(Integer topicId, TopicCreateRequest topicRequest) {
        Topic existingTopic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic","id",String.valueOf(topicId)));
        existingTopic.setName(topicRequest.getName());
        topicRepo.save(existingTopic);
        String key = RedisKey.generateTopicKey();
        stringRedisTemplate.delete(key);
    }

    @Transactional
    @Override
    public void deleteTopic(Integer topicId) {
        topicRepo.deleteById(topicId);
        String key = RedisKey.generateTopicKey();
        stringRedisTemplate.delete(key);
    }

    @Override
    public ListTopicResponse getAllTopics(String keyword, PagingSearch pagingSearch) {
        Page<Topic> topics = topicRepo.findByKeyword(keyword, pagingSearch.toPageable());
        List<TopicResponse> topicResponses = topics.getContent()
                .stream()
                .map(t -> TopicResponse.builder()
                        .topicName(t.getName())
                        .topicId(t.getTopicId())
                        .build())
                .toList();
        return ListTopicResponse.builder()
                .topics(topicResponses)
                .page(topics.getNumber() + 1)
                .totalPages(topics.getTotalPages())
                .build();
    }

    @Override
    public List<TopicStatsResponse> statsTopic() {
        List<Object[]> response = topicRepo.statsTopic();
        List<TopicStatsResponse> topicStatsResponses = response.stream()
                .map(r -> TopicStatsResponse.builder()
                        .name(r[0].toString())
                        .count((long) r[1])
                        .build())
                .toList();
        return topicStatsResponses;
    }

    private List<TopicResponse> getTopicsFromRedis() throws JsonProcessingException {
        String key = RedisKey.generateTopicKey();
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            List<TopicResponse> topicResponses = objectMapper.readValue(value, new TypeReference<>() {});
            log.debug("topicResponses: {}", topicResponses);
            return topicResponses;
        }
        return null;
    }
}
