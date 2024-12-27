package com.vincent.domain.member.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.TestJwtProvider;
import com.vincent.domain.member.TestMemberDataService;
import com.vincent.config.redis.service.TestRedisService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.service.MemberService.LoginResult;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import com.vincent.domain.member.service.data.MemberDataService;
import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.config.redis.service.RedisService;
import com.vincent.exception.handler.JwtInvalidHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    private MemberDataService memberDataService;
    private RedisService redisService;
    private JwtProvider jwtProvider;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberDataService = new TestMemberDataService();
        redisService = new TestRedisService();
        jwtProvider = new TestJwtProvider();
        memberService = new MemberService(memberDataService, redisService, jwtProvider);
    }

    @Test
    public void 로그인성공() {
        // given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        memberDataService.save(member);

        String email = "test@gmail.com";
        // when
        LoginResult result = memberService.login(email, SocialType.KAKAO);
        // then
        Assertions.assertNotNull(result.getAccessToken());
        Assertions.assertNotNull(result.getRefreshToken());
    }

    @Test
    public void 로그인성공_회원가입() {
        // given
        String email = "test@gmail.com";
        SocialType socialType = SocialType.KAKAO;
        Member member = Member.builder()
            .id(1L)
            .email(email)
            .socialType(socialType)
            .build();

        // when
        LoginResult result = memberService.login(email, socialType);

        // then
        Assertions.assertNotNull(result.getAccessToken());
        Assertions.assertNotNull(result.getRefreshToken());
    }

    @Test
    public void 토큰재발급() {
        // given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        memberDataService.save(member);
        String refreshToken = redisService.generateRefreshToken(member).getRefreshToken();

        // when
        ReissueResult result = memberService.reissue(refreshToken);

        // then
        Assertions.assertNotNull(result.getAccessToken());
        Assertions.assertNotNull(result.getRefreshToken());
    }

    @Test
    public void 로그아웃_리프레시토큰존재() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        memberDataService.save(member);

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(),
            member.getSocialType());
        String refreshToken = redisService.generateRefreshToken(member).getRefreshToken();

        //when
        memberService.logout(accessToken, refreshToken);

        //then
        assertFalse(redisService.exists(member.getId()), "리프레시 토큰이 Redis에서 삭제되지 않았습니다.");
        assertTrue(redisService.isBlacklisted(accessToken), "엑세스 토큰이 블랙리스트에 추가되지 않았습니다.");
    }

    @Test
    public void 로그아웃_리프레시토큰존재하지않음() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        memberDataService.save(member);
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(),
            member.getSocialType());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());

        //when
        memberService.logout(accessToken, refreshToken);

        //then
        assertFalse(redisService.exists(member.getId()), "리프레시 토큰이 Redis에서 삭제되지 않았습니다.");
        assertTrue(redisService.isBlacklisted(accessToken), "엑세스 토큰이 블랙리스트에 추가되지 않았습니다.");
    }

    @Test
    public void 회원탈퇴() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        memberDataService.save(member);

        //when
        memberService.withdraw(member.getId());

        //then
        assertTrue(member.isWithdraw(), "회원 상태가 탈퇴 상태로 변경되지 않았습니다.");
        Member optionalMember = memberDataService.findById(member.getId());
        assertNotNull(optionalMember, "데이터베이스에서 회원을 찾을 수 없습니다.");
        assertTrue(optionalMember.isWithdraw(), "데이터베이스에 저장된 회원 상태가 탈퇴 상태가 아닙니다.");
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
