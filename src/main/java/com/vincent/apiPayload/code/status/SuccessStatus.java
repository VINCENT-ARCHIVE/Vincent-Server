package com.vincent.apiPayload.code.status;

import com.vincent.apiPayload.code.BaseCode;
import com.vincent.apiPayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK,"COMMON200","성공입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason(){
        return com.vincent.apiPayload.code.ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus(){
        return com.vincent.apiPayload.code.ReasonDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(true)
                .message(message)
                .code(code)
                .build();
    }
}