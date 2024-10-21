package com.vincent.domain.member.repository;

import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndSocialType(String email, SocialType socialType);
}
