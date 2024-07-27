package com.vincent.domain.member.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.exception.handler.ErrorHandler;
import com.vincent.redis.entity.RefreshToken;
import com.vincent.redis.service.RedisService;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
     * 사용자 로그인 AccessToken, refreshToken 반환
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

    @Transactional
    public ReissueResult reissue(String token) {
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        //memberId로 Redis에서 리프레시 토큰 조회
        RefreshToken refreshToken = redisService.findRefreshToken(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED));

        //탈취 검증
        //만약 사용자가 보낸 토큰이랑 Redis에서 조회한 토큰이 다르다면 토큰이 탈취되었을 가능성이 있다
        if (!refreshToken.getRefreshToken().equals(token)) {
            redisService.delete(refreshToken);
            throw new ErrorHandler(ErrorStatus.ANOTHER_USER);
        }

        Member member = memberRepository.findById(refreshToken.getMemberId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        String newRefreshToken = redisService.reGenerateRefreshToken(member, refreshToken)
                .getRefreshToken();

        return new ReissueResult(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        Long memberIdFromRefresh = jwtProvider.getMemberId(refreshToken);

        //리프레시 토큰 삭제
        Optional<RefreshToken> optionalRefreshToken = redisService.findRefreshToken(
                memberIdFromRefresh);
        optionalRefreshToken.ifPresent(redisService::delete);

        //AccessToken의 유효시간을 가져와서 블랙리스트 생성
        redisService.blacklist(accessToken);
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
