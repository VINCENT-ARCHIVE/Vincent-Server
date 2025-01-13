package com.vincent.domain.iot.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.IotRepository;
import com.vincent.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IotDataService {

    private final IotRepository iotRepository;

    public Iot save(Iot iot) {
        return iotRepository.save(iot);
    }

    public Iot findByDeviceId(Long deviceId) {
        return iotRepository.findIotByDeviceId(deviceId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.IOT_NOT_FOUND));
    }

}
