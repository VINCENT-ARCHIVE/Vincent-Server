package com.vincent.domain.member.service;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
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

  private final MemberRepository memberRepository;
  private final JwtProvider jwtProvider;

  @Transactional
  public LoginResult login(String email) {
    Optional<Member> optionalMember = memberRepository.findByEmail(email);
    Member member = optionalMember.orElseGet(() -> {
      Member newMember = Member.builder()
          .email(email)
          .build();
      return memberRepository.save(newMember);
    });

    String accessToken = jwtProvider.createJwt(member.getId(), member.getEmail());
    LocalDateTime current = LocalDateTime.now();
    LocalDateTime accessExpireTime = current.plusMinutes(30);
    return new LoginResult(accessToken, accessExpireTime);
  }

  @Getter
  @AllArgsConstructor
  public static class LoginResult {

    private String accessToken;
    private LocalDateTime accessExpireTime;
  }
}
