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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    void 소켓_사용여부_업데이트_성공() {
        // given
        Long deviceId = 1L;
        String redisKey = "iot:" + deviceId;

        // Redis 데이터 추가 (40개 0, 60개 1)
        List<Object> redisData = new ArrayList<>();
        for (int i = 0; i < 40; i++) redisData.add("0");
        for (int i = 0; i < 60; i++) redisData.add("1");
        when(redisService.getList(redisKey, 0, -1)).thenReturn(redisData);

        // Mock된 Socket 및 Iot 생성
        Socket socket = Socket.builder().id(1L).name("socket1").isUsing(false).build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        when(iotDataService.findByDeviceId(deviceId)).thenReturn(iot);
        when(socketDataService.findById(socket.getId())).thenReturn(socket);

        // when
        boolean result = iotService.updateIsSocketUsing(deviceId);

        // then
        Assertions.assertTrue(result);
        verify(redisService, times(1)).getList(redisKey, 0, -1);
        verify(redisService, times(1)).delete(redisKey);
    }



    @Test
    void 소켓_사용여부_업데이트_실패_데이터부족() {
        // given
        Long deviceId = 2L;
        String redisKey = "iot:" + deviceId;

        when(redisService.getList(redisKey, 0, -1)).thenReturn(new ArrayList<>());

        // when
        boolean result = iotService.updateIsSocketUsing(deviceId);

        // then
        assertFalse(result);
        verify(redisService, times(1)).getList(redisKey, 0, -1);
        verify(redisService, never()).delete(redisKey);
    }

    @Test
    void 소켓_사용여부_업데이트_성공_0이_더많은경우() {
        // given
        Long deviceId = 3L;
        String redisKey = "iot:" + deviceId;

        List<Object> redisData = new ArrayList<>();
        for (int i = 0; i < 60; i++) redisData.add("0");
        for (int i = 0; i < 40; i++) redisData.add("1");

        when(redisService.getList(redisKey, 0, -1)).thenReturn(redisData);

        Socket socket = Socket.builder().id(2L).name("socket2").isUsing(true).build();
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();

        when(iotDataService.findByDeviceId(deviceId)).thenReturn(iot);
        when(socketDataService.findById(socket.getId())).thenReturn(socket);

        // when
        boolean result = iotService.updateIsSocketUsing(deviceId);

        // then
        assertTrue(result);
        assertFalse(socket.getIsUsing());
        verify(redisService, times(1)).getList(redisKey, 0, -1);
        verify(redisService, times(1)).delete(redisKey);

    }
}
