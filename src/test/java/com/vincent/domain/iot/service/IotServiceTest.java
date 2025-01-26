package com.vincent.domain.iot.service;

import com.vincent.config.redis.service.RedisService;
import com.vincent.config.redis.service.TestRedisService;
import com.vincent.domain.iot.TestIotRepository;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.entity.enums.MotionStatus;
import com.vincent.domain.iot.repository.IotRepository;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.socket.TestSocketRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IotServiceTest {

    private IotService iotService;
    private RedisService redisService;
    private IotDataService iotDataService;
    private SocketDataService socketDataService;

    private Iot iot;
    private Socket socket;

    @BeforeEach
    void setUp() {
        redisService = new TestRedisService();
        iotDataService = new IotDataService(new TestIotRepository());
        socketDataService = new SocketDataService(new TestSocketRepository());
        iotService = new IotService(iotDataService, socketDataService, redisService);
    }



    @Test
    void IOT장치_등록() {
        //given
        Long deviceId = 1L;
        String socketId = "name";
        Socket socket = Socket.builder()
                .id(1L)
                .name("name")
                .isUsing(false)
                .build();
        socketDataService.save(socket);
        //when
        Iot result = iotService.create(deviceId, socketId);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(deviceId, result.getDeviceId());
        Assertions.assertEquals(socketId, result.getSocket().getName());
    }

    @Test
    void IOT_상태갱신_움직임있음() {
        // Given
        Long deviceId = 1L;
        int motionStatus = 1;
        Socket socket = Socket.builder()
                .id(1L)
                .name("name")
                .isUsing(false)
                .build();
        Iot iot = Iot.builder()
                .id(1L)
                .deviceId(deviceId)
                .socket(socket)
                .motionStatus(MotionStatus.INACTIVE)
                .build();
        iotDataService.save(iot);

        //when
        iotService.updateSocketStatus(deviceId, motionStatus);

        // Then
        assertEquals(MotionStatus.ACTIVE, iot.getMotionStatus());
        assertTrue(iot.getSocket().getIsUsing());
    }

    @Test
    void IOT_상태갱신_움직임없음() {
        // Given
        Long deviceId = 1L;
        int motionStatus = 0;
        Socket socket = Socket.builder()
                .id(1L)
                .name("name")
                .isUsing(true)
                .build();
        Iot iot = Iot.builder()
                .id(1L)
                .deviceId(deviceId)
                .socket(socket)
                .motionStatus(MotionStatus.INACTIVE)
                .build();
        iotDataService.save(iot);

        //when
        iotService.updateSocketStatus(deviceId, motionStatus);

        // Then
        assertEquals(MotionStatus.INACTIVE, iot.getMotionStatus());
    }

    @Test
    void 소켓사용여부갱신_움직임없음() {
        // Given
        Long deviceId = 1L;
        Socket socket = Socket.builder()
                .id(1L)
                .name("name")
                .isUsing(true)
                .build();
        Iot iot = Iot.builder()
                .id(1L)
                .deviceId(deviceId)
                .socket(socket)
                .motionStatus(MotionStatus.INACTIVE)
                .build();
        iotDataService.save(iot);
        //when
        iotService.updateSocketIsUsing(deviceId);

        // Then
        assertFalse(iot.getSocket().getIsUsing());
    }

    @Test
    void 소켓사용여부갱신_움직임있음() {
        // Given
        Long deviceId = 1L;
        Socket socket = Socket.builder()
                .id(1L)
                .name("name")
                .isUsing(true)
                .build();
        Iot iot = Iot.builder()
                .id(1L)
                .deviceId(deviceId)
                .socket(socket)
                .motionStatus(MotionStatus.ACTIVE)
                .build();
        iotDataService.save(iot);

        //when
        iotService.updateSocketIsUsing(deviceId);

        // Then
        assertTrue(iot.getSocket().getIsUsing());
    }

}
