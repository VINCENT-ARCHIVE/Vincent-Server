package com.vincent.domain.building.repository;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.custombuilding.CustomBuildingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Long>, CustomBuildingRepository {



}
