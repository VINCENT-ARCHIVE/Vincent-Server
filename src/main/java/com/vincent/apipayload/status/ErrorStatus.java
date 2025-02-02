package com.vincent.apipayload.status;

import com.vincent.apipayload.code.BaseCode;
import com.vincent.apipayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 가장 일반적인 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    ANOTHER_USER(HttpStatus.UNAUTHORIZED, "COMMON404", "토큰 탈취 위협이 있습니다. 다시 로그인하시기 바랍니다."),

    //JWT

    JWT_BAD_REQUEST(HttpStatus.UNAUTHORIZED, "JWT4001", "잘못된 JWT 서명입니다."),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4002", "액세스 토큰이 만료되었습니다."),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4003",
            "리프레시 토큰이 만료되었습니다. 다시 로그인하시기 바랍니다."),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4004", "지원하지 않는 JWT 토큰입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4005", "유효한 JWT 토큰이 없습니다."),
    JWT_TOKEN_LOGOUT(HttpStatus.BAD_REQUEST, "JWT4006", "로그아웃 처리된 토큰입니다."),


    // 북마크
    BOOKMARK_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "BOOKMARK4001", "이미 북마크에 추가된 콘센트입니다."),
    BOOKMARK_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "BOOKMARK4002", "이미 북마크에서 삭제된 콘센트입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK4003", "북마크를 찾을 수 없습니다."),

    // 멤버
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "회원 정보를 찾을 수 없습니다."),

    // 소켓
    SOCKET_NOT_FOUND(HttpStatus.NOT_FOUND, "SOCKET4001", "소켓 정보를 찾을 수 없습니다."),

    // 빌딩
    BUILDING_NOT_FOUND(HttpStatus.NOT_FOUND, "BUILDING4001", "빌딩 정보를 찾을 수 없습니다."),

    // 층
    FLOOR_NOT_FOUND(HttpStatus.NOT_FOUND, "FLOOR4001", "층 정보를 찾을 수 없습니다."),

    // 공간
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE4001", "공간 정보를 찾을 수 없습니다."),

    //이미지
    IMAGE_CONVERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE4001", "이미지 변환 중 에러가 발생했습니다."),

    //Iot
    IOT_NOT_FOUND(HttpStatus.NOT_FOUND, "IOT4001", "IOT 정보를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(false)
                .message(message)
                .code(code)
                .build();
    }
}
