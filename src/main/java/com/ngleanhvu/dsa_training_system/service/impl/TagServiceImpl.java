package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.response.TagResponse;
import com.ngleanhvu.dsa_training_system.mappter.TagMapper;
import com.ngleanhvu.dsa_training_system.redis.RedisKey;
import com.ngleanhvu.dsa_training_system.repo.TagRepo;
import com.ngleanhvu.dsa_training_system.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepo tagRepo;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<TagResponse> getTags() throws JsonProcessingException {
        List<TagResponse> tagResponses = getTagsFromRedis();

        if (tagResponses == null) {
            tagResponses = tagRepo.findAll().stream()
                    .map(TagMapper::mapResponse)
                    .toList();

            String key = RedisKey.generateTagKey();
            String value = objectMapper.writeValueAsString(tagResponses);
            stringRedisTemplate.opsForValue().set(key, value);

            log.info("tagResponses fetched from DB and cached in Redis: {}", tagResponses);
        } else {
            log.info("tagResponses fetched from Redis: {}", tagResponses);
        }

        return tagResponses;
    }

    private List<TagResponse> getTagsFromRedis() throws JsonProcessingException {
        String key = RedisKey.generateTagKey();
        String value = stringRedisTemplate.opsForValue().get(key);

        if (value != null) {
            log.info("value from Redis: {}", value);
            List<TagResponse> tagResponseList = objectMapper.readValue(value, new TypeReference<>() {});
            log.info("tagResponseList deserialized: {}", tagResponseList);
            return tagResponseList;
        }

        return null;
    }

}
