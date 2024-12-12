package com.vincent.config.redis.service;

import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.config.redis.repository.RefreshTokenRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestRefreshTokenRepository implements RefreshTokenRepository {

    List<RefreshToken> refreshTokens = new ArrayList<>();

    @Override
    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return refreshTokens.stream()
            .filter(token -> token.getRefreshToken().equals(refreshToken))
            .findFirst();
    }

    @Override
    public Optional<RefreshToken> findById(Long aLong) {
        return refreshTokens.stream()
            .filter(token -> token.getMemberId().equals(aLong))
            .findFirst();
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        refreshTokens.removeIf(token -> token.getMemberId().equals(memberId));
    }

    @Override
    public <S extends RefreshToken> S save(S entity) {
        refreshTokens.add(entity);
        return entity;
    }

    @Override
    public boolean existsById(Long memberId) {
        return refreshTokens.stream()
            .anyMatch(token -> token.getMemberId().equals(memberId));
    }

    @Override
    public <S extends RefreshToken> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Iterable<RefreshToken> findAll() {
        return null;
    }

    @Override
    public Iterable<RefreshToken> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(RefreshToken entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends RefreshToken> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
