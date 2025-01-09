package com.vincent.domain.iot.service;

import com.vincent.config.redis.service.TestRedisTemplate;
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
import org.mockito.Mockito;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

class IotServiceTest {
    private IotService iotService;
    private IotDataService iotDataService;
    private SocketDataService socketDataService;
    private TestRedisTemplate redisTemplate;

    @BeforeEach
    void setUp(){
        IotRepository iotRepository = new TestIotRepository();
        iotDataService = new IotDataService(iotRepository);
        SocketRepository socketRepository = new TestSocketRepository();
        socketDataService = new SocketDataService(socketRepository);

        // TestRedisTemplate 사용
        redisTemplate = new TestRedisTemplate();
        iotService = new IotService(iotDataService, socketDataService, redisTemplate);
    }

    @Test
    void IOT장치_등록() {
        //given
        Long deviceId = 1L;
        String socketId = "name";
        Socket socket = Socket.builder().id(1L).name("name").build();
        socketDataService.save(socket);

        //when
        Iot iot = iotService.create(deviceId, socketId);

        //then
        Assertions.assertNotNull(iot);
        Assertions.assertEquals(iot.getDeviceId(), deviceId);
        Assertions.assertEquals(iot.getSocket().getName(), socketId);
    }

    @Test
    void IOT_데이터_저장() {
        // given
        Long deviceId = 1L;
        int isUsing = 1;
        String redisKey = "iot:" + deviceId;

        // when
        iotService.saveIotData(deviceId, isUsing);

        // then
        List<Object> savedData = redisTemplate.getList(redisKey);
        Assertions.assertNotNull(savedData);
        Assertions.assertEquals(1, savedData.size());
        Assertions.assertEquals(String.valueOf(isUsing), savedData.get(0));
    }

    @Test
    void 소켓_사용여부_업데이트() {
        // given
        Long deviceId = 1L;
        String socketId = "name";
        Socket socket = Socket.builder().id(1L).name(socketId).build();
        socketDataService.save(socket);

        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();
        iotDataService.save(iot);

        String redisKey = "iot:" + deviceId;

        // Redis에 70개의 상태값 저장 (0: 35개, 1: 35개)
        for (int i = 0; i < 70; i++) {
            redisTemplate.addToList(redisKey, i % 2 == 0 ? "0" : "1");
        }

        // when
        boolean result = iotService.updateIsSocketUsing(deviceId);

        // then
        Assertions.assertTrue(result);
        Socket updatedSocket = socketDataService.findById(socket.getId());
        Assertions.assertTrue(updatedSocket.getIsUsing());
        Assertions.assertNull(redisTemplate.getList(redisKey)); // 데이터 삭제 확인
    }

    @Test
    void 소켓_사용여부_업데이트_실패_데이터부족() {
        // given
        Long deviceId = 1L;

        // when
        boolean result = iotService.updateIsSocketUsing(deviceId);

        // then
        Assertions.assertFalse(result);
    }
}
