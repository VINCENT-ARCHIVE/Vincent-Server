package com.vincent.domain.member.entity.enums;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.exception.handler.ErrorHandler;

public enum SocialType {
    KAKAO, APPLE;

    public static SocialType fromString(String socialType) {
        try {
            return SocialType.valueOf(socialType.toUpperCase()); // 문자열을 대문자로 변환 후 매칭
        } catch (Exception e) {
            throw new ErrorHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}
