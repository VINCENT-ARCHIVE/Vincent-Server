package com.vincent.domain.member.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.redis.entity.RefreshToken;
import com.vincent.redis.service.RedisService;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    /**
     * 사용자 로그인
     * AccessToken, refreshToken 반환
     */
    @Transactional
    public LoginResult login(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member = optionalMember.orElseGet(() -> {
            Member newMember = Member.builder()
                .email(email)
                .build();
            return memberRepository.save(newMember);
        });

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        RefreshToken refreshToken = redisService.generateRefreshToken(member);

        return new LoginResult(accessToken, refreshToken.getRefreshToken());
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResult {
        private String accessToken;
        private String refreshToken;
    }
}
