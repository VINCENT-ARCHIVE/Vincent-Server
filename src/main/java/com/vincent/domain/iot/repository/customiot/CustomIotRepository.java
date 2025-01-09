package com.vincent.domain.iot.repository.customiot;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.iot.entity.Iot;
import java.util.List;
import java.util.Optional;

public interface CustomIotRepository {

    Optional<Iot> findByDeviceId(Long deviceId);
}
