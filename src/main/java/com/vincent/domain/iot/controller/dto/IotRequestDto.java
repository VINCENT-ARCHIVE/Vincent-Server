package com.vincent.domain.iot.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class IotRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class create {
        Long deviceId;
        String socketId;
    }
}
