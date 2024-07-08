package com.vincent.apiPayload.exception;

import com.vincent.apiPayload.code.BaseCode;
import com.vincent.apiPayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{

    private final BaseCode code;

    public ReasonDto getErrorReason(){
        return this.code.getReason();
    }

    public ReasonDto getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }

}