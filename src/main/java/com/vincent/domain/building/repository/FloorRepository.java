package com.vincent.domain.building.repository;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.repository.customfloor.CustomFloorRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloorRepository extends JpaRepository<Floor, Long>, CustomFloorRepository {


    Floor findByBuildingAndLevel(Building building, Integer level);



}
