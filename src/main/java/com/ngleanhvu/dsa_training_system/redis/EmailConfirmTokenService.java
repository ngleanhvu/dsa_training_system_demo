package com.ngleanhvu.dsa_training_system.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailConfirmTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String token, String userId, Duration ttl) {
        String key = "email_confirm:" + token;
        redisTemplate.opsForValue().set(key, userId, ttl);
    }

    public String verifyToken(String token) {
        String key = "email_confirm:" + token;
        String userId = redisTemplate.opsForValue().get(key);
        if (userId != null) {
            redisTemplate.delete(key); // xoá sau khi xác nhận
        }
        return userId;
    }
}

