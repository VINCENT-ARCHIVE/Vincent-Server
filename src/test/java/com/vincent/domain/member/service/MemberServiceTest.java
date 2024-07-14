package com.vincent.domain.member.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import com.vincent.exception.handler.ErrorHandler;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import com.vincent.redis.entity.RefreshToken;
import com.vincent.redis.service.RedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RedisService redisService;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("로그인_기존 회원")
    public void 로그인_기존회원() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("refreshToken")
            .build();

        //when
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(jwtProvider.createAccessToken(member.getId(), member.getEmail())).thenReturn(
            "accessToken");
        when(redisService.generateRefreshToken(member)).thenReturn(refreshToken);

        //then
        MemberService.LoginResult result = memberService.login("test@gmail.com");

        Assertions.assertEquals("accessToken", result.getAccessToken());
        Assertions.assertEquals("refreshToken", result.getRefreshToken());

    }

    @Test
    @DisplayName("로그인_새로운 회원 생성")
    public void 로그인_새로운회원() {
        //given
        String email = "test@gmail.com";
        Member newMember = Member.builder()
            .id(1L)
            .email(email)
            .build();
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("refreshToken")
            .build();

        //when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);
        when(jwtProvider.createAccessToken(newMember.getId(), newMember.getEmail())).thenReturn(
            "accessToken");
        when(redisService.generateRefreshToken(newMember)).thenReturn(refreshToken);

        //then
        MemberService.LoginResult result = memberService.login(email);
        Assertions.assertEquals("accessToken", result.getAccessToken());
        Assertions.assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    public void 토큰재발급성공() {
        //given
        String token = "refreshToken";
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("refreshToken")
            .build();
        RefreshToken newRefreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("newRefreshToken")
            .build();
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();

        //when
        when(jwtProvider.getMemberId(token)).thenReturn(Long.valueOf(1));
        when(redisService.findRefreshToken(1L)).thenReturn(Optional.of(refreshToken));
        when(memberRepository.findById(refreshToken.getMemberId())).thenReturn(Optional.of(member));
        when(jwtProvider.createAccessToken(member.getId(), member.getEmail())).thenReturn(
            "newAccessToken");
        when(redisService.reGenerateRefreshToken(member, refreshToken)).thenReturn(newRefreshToken);

        //then
        ReissueResult result = memberService.reissue(token);
        Assertions.assertEquals("newAccessToken", result.getAccessToken());
        Assertions.assertEquals("newRefreshToken", result.getRefreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 멤버 없음")
    public void 토큰재발급실패_멤버없음() {
        //given
        String token = "refreshToken";
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("refreshToken")
            .build();

        //when
        when(jwtProvider.getMemberId(token)).thenReturn(Long.valueOf(1));
        when(redisService.findRefreshToken(1L)).thenReturn(Optional.of(refreshToken));
        when(memberRepository.findById(refreshToken.getMemberId())).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class, () -> {
            memberService.reissue(token);
        });
        Assertions.assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
        Mockito.verify(redisService, never())
            .reGenerateRefreshToken(any(Member.class), any(RefreshToken.class));
        Mockito.verify(jwtProvider, never())
            .createAccessToken(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 오류(Invalid)")
    public void 토큰재발급실패_Invalid() {
        //given
        String token = "Invalid refreshToken";
        doThrow(new JwtInvalidHandler("Invalid Token Exception")).when(jwtProvider)
            .validateToken(token);
        //when/then
        JwtInvalidHandler thrown = Assertions.assertThrows(JwtInvalidHandler.class, () -> {
            memberService.reissue(token);
        });
        Assertions.assertEquals("Invalid Token Exception", thrown.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 오류(Expired)")
    public void 토큰재발급실패_Expired() {
        //given
        String token = "Expired refreshToken";
        doThrow(new JwtExpiredHandler("Expired Token Exception")).when(jwtProvider)
            .validateToken(token);

        //when/then
        JwtExpiredHandler thrown = Assertions.assertThrows(JwtExpiredHandler.class, () -> {
            memberService.reissue(token);
        });
        Assertions.assertEquals("Expired Token Exception", thrown.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 탈취 위험")
    public void 토큰재발급실패_탈취위험() {
        //given
        String token = "previous refreshToken";
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("new refreshToken")
            .build();

        //when
        when(jwtProvider.getMemberId(token)).thenReturn(1L);
        when(redisService.findRefreshToken(1L)).thenReturn(Optional.of(refreshToken));

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class, () -> {
            memberService.reissue(token);
        });
        Assertions.assertEquals(ErrorStatus.ANOTHER_USER, thrown.getCode());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 만료")
    public void 토큰재발급실퍠_토큰만료() {
        //given
        String token = "refreshToken";

        //when
        when(jwtProvider.getMemberId(token)).thenReturn(1L);
        when(redisService.findRefreshToken(1L)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class, () -> {
            memberService.reissue(token);
        });
        Assertions.assertEquals(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED, thrown.getCode());
    }

}
