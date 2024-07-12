package com.vincent.exception.handler;

import com.vincent.apiPayload.code.BaseCode;
import com.vincent.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseCode baseCode){
        super(baseCode);
    }
}
