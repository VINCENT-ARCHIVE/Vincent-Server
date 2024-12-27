package com.vincent.domain.member.service.data;



import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.member.TestMemberRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberDataServiceTest {

    private TestMemberRepository memberRepository;
    private MemberDataService memberDataService;

    @BeforeEach()
    void setUp() {
        memberRepository = new TestMemberRepository();
        memberDataService = new MemberDataService(memberRepository);
    }

    @Test
    void 이메일로사용자찾기() {
        // given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();
        memberRepository.save(member);

        // when
        Optional<Member> result = memberDataService.findByEmail("test@gmail.com");

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get(), member);
    }

    @Test
    void 이메일과소셜로사용자찾기() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .socialType(SocialType.KAKAO)
            .build();
        memberRepository.save(member);

        //when
        Optional<Member> result = memberDataService.findByEmailAndSocialType("test@gmail.com",
            SocialType.KAKAO);
        //then
        Assertions.assertEquals(result.get(), member);
    }

    @Test
    void 아이디로사용자찾기() {
        //given
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();
        memberRepository.save(member);

        //when
        Member result = memberDataService.findById(1L);

        //then
        Assertions.assertEquals(result, member);
    }

    @Test
    void 아이디로사용자찾기_실패() {
        //given
        Long id = 1L;

        //when
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> memberDataService.findById(id));
        //then
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
        Member result = memberDataService.save(member);

        //then
        Assertions.assertEquals(result, member);
    }
}
