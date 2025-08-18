package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.response.TopicResponse;
import com.ngleanhvu.dsa_training_system.redis.RedisKey;
import com.ngleanhvu.dsa_training_system.repo.TopicRepo;
import com.ngleanhvu.dsa_training_system.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
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
