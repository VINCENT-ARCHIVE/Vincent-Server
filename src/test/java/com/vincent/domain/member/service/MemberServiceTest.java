package com.vincent.domain.member.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.service.MemberService.LoginResult;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import com.vincent.domain.member.service.data.MemberDataService;
import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.config.redis.service.RedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberDataService memberDataService;
    @Mock
    private RedisService redisService;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void 로그인성공() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        String email = "test@gmail.com";
        String accessToken = "access";
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(member.getId())
            .refreshToken("refresh")
            .build();

        //when
        when(memberDataService.findByEmailAndSocialType(email, SocialType.KAKAO)).thenReturn(
            Optional.of(member));
        when(jwtProvider.createAccessToken(member.getId(), member.getEmail(),
            member.getSocialType())).thenReturn(accessToken);
        when(redisService.generateRefreshToken(member)).thenReturn(refreshToken);
        LoginResult result = memberService.login(email, SocialType.KAKAO);

        //then
        verify(memberDataService, times(1)).findByEmailAndSocialType(email, SocialType.KAKAO);
        verify(jwtProvider, times(1)).createAccessToken(1L, "test@gmail.com", SocialType.KAKAO);
        verify(redisService, times(1)).generateRefreshToken(member);
        Assertions.assertEquals(result.getAccessToken(), accessToken);
        Assertions.assertEquals(result.getRefreshToken(), refreshToken.getRefreshToken());

    }

    @Test
    public void 로그인성공_회원가입() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        String email = "test@gmail.com";
        String accessToken = "access";
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(member.getId())
            .refreshToken("refresh")
            .build();

        //when
        when(memberDataService.findByEmailAndSocialType(email, SocialType.KAKAO)).thenReturn(
            Optional.empty());
        when(memberDataService.save(any(Member.class))).thenReturn(member);
        when(jwtProvider.createAccessToken(member.getId(), member.getEmail(),
            member.getSocialType())).thenReturn(accessToken);
        when(redisService.generateRefreshToken(member)).thenReturn(refreshToken);
        LoginResult result = memberService.login(email, SocialType.KAKAO);

        //then
        verify(memberDataService, times(1)).findByEmailAndSocialType(email, SocialType.KAKAO);
        verify(memberDataService, times(1)).save(any(Member.class));
        verify(jwtProvider, times(1)).createAccessToken(1L, "test@gmail.com", SocialType.KAKAO);
        verify(redisService, times(1)).generateRefreshToken(member);
        Assertions.assertEquals(result.getAccessToken(), accessToken);
        Assertions.assertEquals(result.getRefreshToken(), refreshToken.getRefreshToken());
    }

    @Test
    public void 토큰재발급() {
        //given
        String token = "token";
        String newToken = "token2";
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(member.getId())
            .refreshToken("refresh")
            .build();
        RefreshToken refreshToken2 = RefreshToken.builder()
            .memberId(member.getId())
            .refreshToken("refresh2")
            .build();

        //when
        doNothing().when(jwtProvider).validateToken(token);
        when(jwtProvider.getMemberId(token)).thenReturn(1L);
        when(redisService.findByMemberId(1L)).thenReturn(refreshToken);
        when(memberDataService.findById(1L)).thenReturn(member);
        when(jwtProvider.createAccessToken(member.getId(), member.getEmail(),
            member.getSocialType())).thenReturn("token2");
        when(redisService.regenerateRefreshToken(member, refreshToken)).thenReturn(refreshToken2);
        ReissueResult result = memberService.reissue(token);

        //then
        verify(jwtProvider, times(1)).getMemberId(token);
        verify(jwtProvider, times(1)).createAccessToken(member.getId(), member.getEmail(),
            member.getSocialType());
        verify(redisService, times(1)).regenerateRefreshToken(member, refreshToken);
        Assertions.assertEquals(result.getAccessToken(), newToken);
        Assertions.assertEquals(result.getRefreshToken(), refreshToken2.getRefreshToken());
    }

    @Test
    public void 로그아웃_리프레시토큰존재() {
        //given
        String accessToken = "access";
        String refreshToken = "refresh";
        Long memberId = 1L;

        //when
        when(jwtProvider.getMemberId(refreshToken)).thenReturn(memberId);
        when(redisService.exists(memberId)).thenReturn(true);
        doNothing().when(redisService).delete(memberId);
        doNothing().when(redisService).blacklist(accessToken);
        memberService.logout(accessToken, refreshToken);

        //then
        verify(jwtProvider, times(1)).getMemberId(refreshToken);
        verify(redisService, times(1)).exists(memberId);
        verify(redisService, times(1)).delete(memberId);
        verify(redisService, times(1)).blacklist(accessToken);
    }

    @Test
    public void 로그아웃_리프레시토큰존재하지않음() {
        //given
        String accessToken = "access";
        String refreshToken = "refresh";
        Long memberId = 1L;

        //when
        when(jwtProvider.getMemberId(refreshToken)).thenReturn(memberId);
        when(redisService.exists(memberId)).thenReturn(false);
        doNothing().when(redisService).blacklist(accessToken);
        memberService.logout(accessToken, refreshToken);

        //then
        verify(jwtProvider, times(1)).getMemberId(refreshToken);
        verify(redisService, times(1)).exists(memberId);
        verify(redisService, times(1)).blacklist(accessToken);
    }

    @Test
    public void 회원탈퇴() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();

        //when
        when(memberDataService.findById(memberId)).thenReturn(member);
        memberService.withdraw(memberId);

        //then
        verify(memberDataService, times(1)).findById(memberId);
        Assertions.assertEquals(member.isWithdraw(), true);

    }

//
//    @Test
//    @DisplayName("회원탈퇴 - 성공")
//    public void 회원탈퇴(){
//        //given
//        Long memberId = 1L;
//        Member member = Member.builder()
//            .id(1L)
//            .email("test@gmail.com")
//            .build();
//        //when
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//        memberService.withdraw(memberId);
//
//        //then
//        Assertions.assertTrue(member.isWithdraw());
//    }
//
//    @Test
//    @DisplayName("회원탈퇴 - 실패")
//    public void 회원탈퇴실패(){
//        //given
//        Long memberId = 1L;
//
//        //when
//        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
//        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class, () -> {
//            memberService.withdraw(memberId);
//        });
//
//        //then
//        Assertions.assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
//
//    }
}
