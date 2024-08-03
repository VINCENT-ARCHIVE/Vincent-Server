package com.vincent.redis.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.redis.entity.RefreshToken;
import com.vincent.redis.repository.RefreshTokenRepository;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    /**
     * 리프레시 토큰 발급
     */
    public RefreshToken generateRefreshToken(Member member) {
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());
        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .memberId(member.getId())
                        .refreshToken(refreshToken)
                        .build()
        );
    }

    /**
     * 리프레시 토큰 찾기
     */
    public Optional<RefreshToken> findRefreshToken(Long memberId) {
        return refreshTokenRepository.findById(memberId);
    }

    /**
     * 리프레시 토큰 재발급
     */
    public RefreshToken reGenerateRefreshToken(Member member, RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());
        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .memberId(member.getId())
                        .refreshToken(newRefreshToken)
                        .build()
        );
    }

    /**
     * 리프레시 토큰 삭제
     */
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    /**
     * 액세스 토큰 블랙리스트 등록
     */
    public void blacklist(String accessToken) {
        Long expireAccessMs = jwtProvider.getExpireAccessMs(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expireAccessMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 액세스 토큰 블랙리스트 확인
     */
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
    }

}
