package com.vincent.domain.member.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.service.data.MemberDataService;
import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.config.redis.service.RedisService;
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

    private final MemberDataService memberDataService;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    /**
     * 사용자 로그인 AccessToken, refreshToken 반환
     */
    @Transactional
    public LoginResult login(String email, SocialType socialType) {
        Optional<Member> optionalMember = memberDataService.findByEmailAndSocialType(email, socialType);
        Member member = optionalMember.orElseGet(() -> {
            Member newMember = Member.builder()
                    .email(email)
                    .socialType(socialType)
                    .build();
            return memberDataService.save(newMember);
        });

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getSocialType());
        RefreshToken refreshToken = redisService.generateRefreshToken(member);

        return new LoginResult(accessToken, refreshToken.getRefreshToken());
    }

    @Transactional
    public ReissueResult reissue(String token) {
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        RefreshToken refreshToken = redisService.findByMemberId(memberId);
        //탈취 검증
        //만약 사용자가 보낸 토큰이랑 Redis에서 조회한 토큰이 다르다면 토큰이 탈취되었을 가능성이 있다
        redisService.verifyTokenNotHijacked(refreshToken, token);

        Member member = memberDataService.findById(refreshToken.getMemberId());

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        String newRefreshToken = redisService.regenerateRefreshToken(member, refreshToken)
                .getRefreshToken();

        return new ReissueResult(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        Long memberIdFromRefresh = jwtProvider.getMemberId(refreshToken);

        if (redisService.exists(memberIdFromRefresh)) {
            redisService.delete(memberIdFromRefresh);
        }

        //AccessToken의 유효시간을 가져와서 블랙리스트 생성
        redisService.blacklist(accessToken);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberDataService.findById(memberId);
        member.delete();
    }


    @Getter
    @AllArgsConstructor
    public static class LoginResult {

        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    public static class ReissueResult {

        private String accessToken;
        private String refreshToken;
    }
}
