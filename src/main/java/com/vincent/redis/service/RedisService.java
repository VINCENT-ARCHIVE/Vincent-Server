package com.vincent.redis.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.redis.entity.RefreshToken;
import com.vincent.redis.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    /**
     * 리프레시 토큰 발급
     */
    @Transactional
    public RefreshToken generateRefreshToken(Member member) {
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());
        return refreshTokenRepository.save(
            RefreshToken.builder()
                .memberId(member.getId())
                .refreshToken(refreshToken)
                .build()
        );
    }
}
