package com.example.backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveDataWithExpiration(String key, String value, long expirationInSeconds) {
        System.out.println(key+"redis키값");
        System.out.println(expirationInSeconds+"시간");
        redisTemplate.opsForValue().set(key, value);
        // expirationInSeconds 초 후에 데이터가 만료되도록 설정
        redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS);
    }

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
