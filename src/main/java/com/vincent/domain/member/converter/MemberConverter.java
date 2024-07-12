package com.vincent.domain.member.converter;

import com.vincent.domain.member.controller.dto.MemberResponseDto;
import java.time.LocalDateTime;

public class MemberConverter {

    public static MemberResponseDto.Login toLoginResponse(String accessToken, LocalDateTime accessExpireTime) {
        return MemberResponseDto.Login.builder()
            .accessToken(accessToken)
            .accessExpireTime(accessExpireTime)
            .build();
    }
}
