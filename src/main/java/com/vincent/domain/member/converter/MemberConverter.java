package com.vincent.domain.member.converter;

import com.vincent.domain.member.controller.dto.MemberResponseDto;
import java.time.LocalDateTime;

public class MemberConverter {

    public static MemberResponseDto.Login toLoginResponse(String accessToken, String refreshToken) {
        return MemberResponseDto.Login.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static MemberResponseDto.Reissue toReissueResponse(String accessToken,
            String refreshToken) {
        return MemberResponseDto.Reissue.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
