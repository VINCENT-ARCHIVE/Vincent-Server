package com.vincent.domain.iot.repository;

import com.vincent.domain.iot.entity.Iot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotRepository extends JpaRepository<Iot, Long> {

    Optional<Iot> findIotByDeviceId(Long deviceId);
}
