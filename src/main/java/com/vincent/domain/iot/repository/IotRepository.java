package com.vincent.domain.iot.repository;

import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.customiot.CustomIotRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotRepository extends JpaRepository<Iot, Long>, CustomIotRepository {



}
