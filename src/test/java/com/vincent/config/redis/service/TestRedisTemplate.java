package com.vincent.config.redis.service;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class TestRedisTemplate extends RedisTemplate<String, Object> {

    public TestRedisTemplate() {
        super();
    }

    public ValueOperations<String, Object> opsForValue() {
        return new FakeValueOperations();
    }

    private final Map<String, Object> store = new HashMap<>();

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        store.put(key, value);
    }

    // 키가 존재하는지 확인
    public Boolean hasKey(String key) {
        return store.containsKey(key);
    }

    // 키 삭제 및 성공 여부 반환
    public Boolean delete(String key) {
        return store.remove(key) != null;
    }

    // 저장소를 비우는 메서드
    public void clear() {
        store.clear();
    }

    // 저장소에 특정 키의 값을 반환하는 메서드 추가
    public Object get(String key) {
        return store.get(key);
    }

    public class FakeValueOperations implements ValueOperations<String, Object> {

        @Override
        public void set(String key, Object value) {

        }

        @Override
        public void set(String key, Object value, long timeout, TimeUnit unit) {
            store.put(key, value);
        }

        @Override
        public Boolean setIfAbsent(String key, Object value) {
            return null;
        }

        @Override
        public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
            return null;
        }

        @Override
        public Boolean setIfPresent(String key, Object value) {
            return null;
        }

        @Override
        public Boolean setIfPresent(String key, Object value, long timeout, TimeUnit unit) {
            return null;
        }

        @Override
        public void multiSet(Map<? extends String, ?> map) {

        }

        @Override
        public Boolean multiSetIfAbsent(Map<? extends String, ?> map) {
            return null;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object getAndDelete(String key) {
            return null;
        }

        @Override
        public Object getAndExpire(String key, long timeout, TimeUnit unit) {
            return null;
        }

        @Override
        public Object getAndExpire(String key, Duration timeout) {
            return null;
        }

        @Override
        public Object getAndPersist(String key) {
            return null;
        }

        @Override
        public Object getAndSet(String key, Object value) {
            return null;
        }

        @Override
        public List<Object> multiGet(Collection<String> keys) {
            return List.of();
        }

        @Override
        public Long increment(String key) {
            return 0L;
        }

        @Override
        public Long increment(String key, long delta) {
            return 0L;
        }

        @Override
        public Double increment(String key, double delta) {
            return 0.0;
        }

        @Override
        public Long decrement(String key) {
            return 0L;
        }

        @Override
        public Long decrement(String key, long delta) {
            return 0L;
        }

        @Override
        public Integer append(String key, String value) {
            return 0;
        }

        @Override
        public String get(String key, long start, long end) {
            return "";
        }

        @Override
        public void set(String key, Object value, long offset) {

        }

        @Override
        public Long size(String key) {
            return 0L;
        }

        @Override
        public Boolean setBit(String key, long offset, boolean value) {
            return null;
        }

        @Override
        public Boolean getBit(String key, long offset) {
            return null;
        }

        @Override
        public List<Long> bitField(String key, BitFieldSubCommands subCommands) {
            return List.of();
        }

        @Override
        public RedisOperations<String, Object> getOperations() {
            return null;
        }
    }
}