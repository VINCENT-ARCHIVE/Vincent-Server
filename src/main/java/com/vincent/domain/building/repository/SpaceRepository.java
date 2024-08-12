package com.vincent.domain.building.repository;

import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {

}
