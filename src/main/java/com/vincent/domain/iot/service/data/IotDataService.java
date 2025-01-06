package com.vincent.domain.iot.service.data;

import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.IotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IotDataService {

    private final IotRepository iotRepository;

    public Iot save(Iot iot) {
        return iotRepository.save(iot);
    }

}
