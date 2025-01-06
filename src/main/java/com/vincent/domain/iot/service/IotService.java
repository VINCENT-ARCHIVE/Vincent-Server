package com.vincent.domain.iot.service;

import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IotService {
    private final IotDataService iotDataService;
    private final SocketDataService socketDataService;


    @Transactional
    public Iot create(Long deviceId, String socketId) {
        Socket socket = socketDataService.findByUniqueId(socketId);
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();
        return iotDataService.save(iot);
    }

}
