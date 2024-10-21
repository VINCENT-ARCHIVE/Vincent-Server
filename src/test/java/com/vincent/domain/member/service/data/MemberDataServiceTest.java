package com.vincent.domain.member.service.data;


import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberDataServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberDataService memberDataService;

    @Test
    void 이메일로사용자찾기() {
        //given
        String email = "test@gmail.com";

        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();

        //when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        //then
        Optional<Member> result = memberDataService.findByEmail(email);
        Assertions.assertEquals(result.get(), member);
    }

    @Test
    void 이메일과소셜로사용자찾기() {
        //given
        String email = "test@gmail.com";
        SocialType socialType = SocialType.KAKAO;

        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();

        //when
        when(memberRepository.findByEmailAndSocialType(email, socialType)).thenReturn(Optional.of(member));

        //then
        Optional<Member> result = memberDataService.findByEmailAndSocialType(email, socialType);
        Assertions.assertEquals(result.get(), member);
    }

    @Test
    void 아이디로사용자찾기() {
        //given
        Long id = 1L;

        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();

        //when
        when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        //then
        Member result = memberDataService.findById(id);
        Assertions.assertEquals(result, member);
    }

    @Test
    void 아이디로사용자찾기_실패() {
        //given
        Long id = 1L;

        //when
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> memberDataService.findById(id));

        Assertions.assertEquals(thrown.getCode(), ErrorStatus.MEMBER_NOT_FOUND);
    }

    @Test
    void 사용자저장() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();

        //when
        when(memberRepository.save(member)).thenReturn(member);

        //then
        Member result = memberDataService.save(member);
        Assertions.assertEquals(result, member);
    }
}
