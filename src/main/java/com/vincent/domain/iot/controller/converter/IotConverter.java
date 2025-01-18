package com.vincent.domain.iot.controller.converter;

import com.vincent.domain.iot.controller.dto.IotResponseDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;

public class IotConverter {

    public static IotResponseDto.IotDataTest toIotDataTest(Long deviceId, boolean isUsing) {
        return IotResponseDto.IotDataTest.builder()
            .deviceId(deviceId)
            .isUsing(isUsing)
            .build();
    }


}
