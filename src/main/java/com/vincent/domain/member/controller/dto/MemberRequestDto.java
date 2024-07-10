package com.vincent.domain.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String email;
    }
}
