package com.vincent.config.redis.service;

import com.vincent.domain.member.TestJwtProvider;

public class TestRedisService extends RedisService {

    public TestRedisService() {
        super(new TestRefreshTokenRepository(), new TestRedisTemplate(), new TestJwtProvider());
    }
}
