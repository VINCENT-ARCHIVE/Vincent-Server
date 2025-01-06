package com.vincent.domain.iot.service.data;

import com.vincent.domain.iot.TestIotRepository;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.IotRepository;
import com.vincent.domain.socket.entity.Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IotDataServiceTest {
    private IotRepository iotRepository;
    private IotDataService iotDataService;
    @BeforeEach
    void setUp(){
        iotRepository = new TestIotRepository();
        iotDataService = new IotDataService(iotRepository);
    }

    @Test
    void IOT장치_등록_성공(){
        //given
        Socket socket = Socket.builder().id(1L).name("name").build();
        Iot iot = Iot.builder().id(1L).deviceId(1L).socket(socket).build();

        //when
        Iot save = iotRepository.save(iot);

        //then
        Assertions.assertNotNull(iot);
        Assertions.assertEquals(iot.getDeviceId(), save.getDeviceId());
        Assertions.assertEquals(iot.getSocket().getName(), save.getSocket().getName());

    }

}
