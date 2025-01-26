package com.vincent.domain.iot.service;

import com.vincent.config.redis.service.RedisService;
import com.vincent.config.redis.service.TestRedisService;
import com.vincent.domain.iot.TestIotRepository;
import com.vincent.domain.iot.entity.Iot;
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

    @InjectMocks
    private IotService iotService;

    @Mock
    private RedisService redisService;

    @Mock
    private IotDataService iotDataService;

    @Mock
    private SocketDataService socketDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void IOT장치_등록() {
        //given
        Long deviceId = 1L;
        String socketId = "name";

        Socket socket = Socket.builder().id(1L).name("name").build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        // Mocking
        when(socketDataService.findByUniqueId(socketId)).thenReturn(socket);
        when(iotDataService.save(any(Iot.class))).thenReturn(iot);

        //when
        Iot result = iotService.create(deviceId, socketId);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(deviceId, result.getDeviceId());
        Assertions.assertEquals(socketId, result.getSocket().getName());

        verify(socketDataService, times(1)).findByUniqueId(socketId);
        verify(iotDataService, times(1)).save(any(Iot.class));
    }

    @Test
    void IoT_상태_업데이트_움직임_있을때() {
        // given
        Long deviceId = 1L;
        int motionStatus = 1;  // 움직임 있음
        String redisKey = "iot:" + deviceId;
        Socket socket = Socket.builder().id(1L).name("name").isUsing(false).build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        // Mocking
        when(iotDataService.findByDeviceId(deviceId)).thenReturn(iot);
        when(redisService.isTTLExpired(redisKey)).thenReturn(true);

        // when
        iotService.updateSocketStatus(deviceId, motionStatus);

        // then
        Assertions.assertTrue(iot.getSocket().getIsUsing());

        verify(redisService, times(1)).updateDeviceStatus(deviceId, motionStatus);
        verify(iotDataService, times(1)).findByDeviceId(deviceId);
    }


    @Test
    void IoT_상태_업데이트_움직임_없을때() throws InterruptedException {
        // given
        Long deviceId = 1L;
        int motionStatus = 0;  // 움직임 없음
        String redisKey = "iot:" + deviceId;
        Socket socket = Socket.builder().id(1L).name("name").isUsing(true).build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        // 비동기 작업을 위한 스케줄러 모킹
        doAnswer(invocation -> {
            // 상태 업데이트가 이루어졌을 때 socket 상태 변경
            iot.getSocket().setIsUsing(false);
            return null;
        }).when(iotService).updateSocketIsUsing(iot, false);  // updateSocketIsUsing 메서드 모킹

        // when
        when(redisService.isTTLExpired(redisKey)).thenReturn(true);
        when(iotDataService.findByDeviceId(deviceId)).thenReturn(iot);

        iotService.updateSocketStatus(deviceId, motionStatus);

        // then
        assertFalse(iot.getSocket().getIsUsing());  // 상태가 'false'로 변경되었는지 확인
    }




    @Test
    void 소켓_상태_변경_성공() {
        // given
        Long deviceId = 1L;
        Socket socket = Socket.builder().id(1L).name("name").isUsing(false).build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        // when
        when(iotDataService.findByDeviceId(deviceId)).thenReturn(iot);
        iotService.updateSocketIsUsing(iot, true);

        // then
        Assertions.assertTrue(iot.getSocket().getIsUsing());
    }

    @Test
    void 소켓_상태_변경_실패() {
        // given
        Long deviceId = 1L;
        Socket socket = Socket.builder().id(1L).name("name").isUsing(true).build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        // when
        when(iotDataService.findByDeviceId(deviceId)).thenReturn(iot);
        iotService.updateSocketIsUsing(iot, true); // 상태가 이미 true이므로 변화 없음

        // then
        Assertions.assertTrue(iot.getSocket().getIsUsing());  // 상태 변화가 없으므로 여전히 true
    }
}
