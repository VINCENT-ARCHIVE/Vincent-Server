package com.vincent.domain.iot.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.iot.TestIotRepository;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.IotRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IotDataServiceTest {
    private IotRepository iotRepository;
    private IotDataService iotDataService;

    @BeforeEach
    void setUp() {
        iotRepository = new TestIotRepository();
        iotDataService = new IotDataService(iotRepository);
    }

    @Test
    void IOT장치_등록_성공() {
        // given
        Socket socket = Socket.builder().id(1L).name("name").build();
        Iot iot = Iot.builder().id(1L).deviceId(1L).socket(socket).build();

        // when
        Iot savedIot = iotDataService.save(iot);

        // then
        Assertions.assertNotNull(savedIot);
        Assertions.assertEquals(iot.getDeviceId(), savedIot.getDeviceId());
        Assertions.assertEquals(iot.getSocket().getName(), savedIot.getSocket().getName());
    }

    @Test
    void IOT장치_조회_성공() {
        // given
        Socket socket = Socket.builder().id(1L).name("name").build();
        Iot iot = Iot.builder().id(1L).deviceId(1L).socket(socket).build();
        iotDataService.save(iot); // IotDataService를 통해 저장

        // when
        Iot targetIot = iotDataService.findByDeviceId(1L);

        // then
        Assertions.assertNotNull(targetIot);
        Assertions.assertEquals(1L, targetIot.getDeviceId());
        Assertions.assertEquals("name", targetIot.getSocket().getName());
    }

    @Test
    void IOT장치_조회_실패_예외발생() {
        // given
        Long nonExistentDeviceId = 99L;

        // when & then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> iotDataService.findByDeviceId(nonExistentDeviceId));

        Assertions.assertEquals(ErrorStatus.IOT_NOT_FOUND, thrown.getCode());
    }
}

