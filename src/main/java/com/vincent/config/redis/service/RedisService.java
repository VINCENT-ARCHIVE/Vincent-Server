package com.vincent.config.redis.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.redis.repository.RefreshTokenRepository;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.exception.handler.ErrorHandler;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        String token = jwtProvider.createRefreshToken(member.getId(), member.getEmail());
        RefreshToken refreshToken = RefreshToken.builder().memberId(member.getId())
            .refreshToken(token).build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * 리프레시 토큰 찾기
     */
    public RefreshToken findByMemberId(Long id) {
        return refreshTokenRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED));
    }

    /**
     * 리프레시 토큰 재발급
     */
    public RefreshToken regenerateRefreshToken(Member member, RefreshToken refreshToken) {
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

    public void delete(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
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

    /**
     * 리프레시 토큰 존재 여부 반환
     */

    public boolean exists(Long memberId) {
        return refreshTokenRepository.existsById(memberId);
    }

    /**
     * 탈취 검증
     */
    public void verifyTokenNotHijacked(RefreshToken refreshToken, String providedToken) {
        if (!refreshToken.getRefreshToken().equals(providedToken)) {
            this.delete(refreshToken);
            throw new ErrorHandler(ErrorStatus.ANOTHER_USER);
        }
    }

}
