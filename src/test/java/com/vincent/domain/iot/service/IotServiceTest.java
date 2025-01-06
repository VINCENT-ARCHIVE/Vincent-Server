package com.vincent.domain.iot.service;

import com.vincent.domain.iot.TestIotRepository;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.IotRepository;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.socket.TestSocketRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.domain.socket.service.data.SocketDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IotServiceTest {
    private IotService iotService;
    private IotDataService iotDataService;
    private SocketDataService socketDataService;

    @BeforeEach
    void setUp(){
        IotRepository iotRepository = new TestIotRepository();
        iotDataService = new IotDataService(iotRepository);
        SocketRepository socketRepository = new TestSocketRepository();
        socketDataService = new SocketDataService(socketRepository);
        iotService = new IotService(iotDataService, socketDataService);
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
}
