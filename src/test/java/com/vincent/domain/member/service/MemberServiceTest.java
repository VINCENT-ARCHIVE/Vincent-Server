package com.vincent.domain.member.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
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
    public void 로그인() {
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
        when(jwtProvider.createAccessToken(member.getId(), member.getEmail())).thenReturn("accessToken");
        when(redisService.generateRefreshToken(member)).thenReturn(refreshToken);

        //then
        MemberService.LoginResult result = memberService.login("test@gmail.com");

        Assertions.assertEquals("accessToken", result.getAccessToken());
        Assertions.assertEquals("refreshToken", result.getRefreshToken());

    }

    @Test
    @DisplayName("로그인_새로운 회원 생성")
    public void 회원가입() {
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
        when(memberRepository.save(Mockito.any(Member.class))).thenReturn(newMember);
        when(jwtProvider.createAccessToken(newMember.getId(), newMember.getEmail())).thenReturn("accessToken");
        when(redisService.generateRefreshToken(newMember)).thenReturn(refreshToken);

        //then
        MemberService.LoginResult result = memberService.login(email);
        Assertions.assertEquals("accessToken", result.getAccessToken());
        Assertions.assertEquals("refreshToken", result.getRefreshToken());
    }
}
