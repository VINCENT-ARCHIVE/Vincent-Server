package com.vincent.domain.member.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDataService {

    private final MemberRepository memberRepository;

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findByEmailAndSocialType(String email, SocialType socialType) {
        return memberRepository.findByEmailAndSocialType(email, socialType);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

}
