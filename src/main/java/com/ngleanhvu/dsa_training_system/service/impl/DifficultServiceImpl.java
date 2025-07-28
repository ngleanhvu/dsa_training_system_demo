package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.response.DifficultResponse;
import com.ngleanhvu.dsa_training_system.redis.RedisKey;
import com.ngleanhvu.dsa_training_system.repo.DifficultyRepo;
import com.ngleanhvu.dsa_training_system.service.DifficultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DifficultServiceImpl implements DifficultService {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final DifficultyRepo difficultyRepo;

    @Override
    public List<DifficultResponse> getDifficulties() throws JsonProcessingException {
        List<DifficultResponse> difficulties = getDifficultiesFromRedis();
        if (difficulties == null) {
            difficulties = difficultyRepo.findAll().stream()
                    .map(d -> DifficultResponse.builder()
                            .difficultId(d.getDifficultyId())
                            .difficultName(d.getName())
                            .build())
                    .toList();
        }
        return difficulties;
    }

    private List<DifficultResponse> getDifficultiesFromRedis() throws JsonProcessingException {
        String key = RedisKey.generateDifficultiesKey();
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            List<DifficultResponse> difficultResponses = objectMapper.readValue(
                    value,
                    new TypeReference<>() {}
            );
            log.debug("difficultResponses: {}", difficultResponses);
            return difficultResponses;
        }
        return null;
    }
}
