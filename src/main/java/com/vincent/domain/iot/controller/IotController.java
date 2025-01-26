package com.vincent.domain.iot.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.redis.service.RedisService;
import com.vincent.domain.iot.controller.converter.IotConverter;
import com.vincent.domain.iot.controller.dto.IotRequestDto;
import com.vincent.domain.iot.controller.dto.IotResponseDto;
import com.vincent.domain.iot.controller.dto.IotResponseDto.IotDataTest;
import com.vincent.domain.iot.service.IotService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class IotController {
    private final IotService iotService;
    private final RedisService redisService;

    @Operation(summary = "Iot 장치 등록하기", description = "IOT 장치의 고유 ID와 가리키는 대상(콘센트 ID) 정보를 입력")
    @PostMapping("/iot")
    public ApiResponse<?> createIot(@RequestBody() IotRequestDto.create create) {
        iotService.create(create.getDeviceId(), create.getSocketId());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "콘센트 사용 여부 변경", description = "IoT 장치로부터 움직임 여부 값을 받아 콘센트 사용 여부를 변경합니다.")
    @PatchMapping("/iot/{deviceId}")
    public ApiResponse<?> updateSocketStatus(
        @PathVariable("deviceId") Long deviceId,
        @RequestParam("motionStatus") int motionStatus) {
        iotService.updateSocketStatus(deviceId, motionStatus);
        return ApiResponse.onSuccess(null);
    }

}
