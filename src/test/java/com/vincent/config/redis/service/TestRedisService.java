package com.vincent.config.redis.service;

import com.vincent.domain.iot.TestIotRepository;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.member.TestJwtProvider;
import com.vincent.domain.socket.TestSocketRepository;
import com.vincent.domain.socket.service.data.SocketDataService;

public class TestRedisService extends RedisService {

    public TestRedisService() {
        super(
            new TestRefreshTokenRepository(),  // RefreshTokenRepository
            new TestRedisTemplate(),          // RedisTemplate
            new TestJwtProvider()            // JwtProvider
        );
    }
}
