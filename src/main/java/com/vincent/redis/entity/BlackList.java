package com.vincent.redis.entity;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "blacklist")
public class BlackList {

    @Indexed
    private String accessToken;

}
