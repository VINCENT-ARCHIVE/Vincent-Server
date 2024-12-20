package com.vincent.domain.member.controller.dto;

import com.vincent.domain.member.entity.enums.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String email;
        private SocialType socialType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reissue {

        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Logout {

        private String accessToken;
        private String refreshToken;
    }
}
