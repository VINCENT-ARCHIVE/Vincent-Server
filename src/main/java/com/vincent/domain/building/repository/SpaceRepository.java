package com.vincent.domain.building.repository;

import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.customspace.CustomSpaceRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;

public interface SpaceRepository extends JpaRepository<Space, Long>, CustomSpaceRepository {



    List<Space> findAllByFloor(Floor floor);



}
