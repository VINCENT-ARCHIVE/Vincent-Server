package com.vincent.domain.building.repository.customBuilding;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.entity.Building;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;

public interface CustomBuildingRepository {

    Page<Building> findByNameContainingOrderBySimilarity(String keyword, PageRequest pageRequest);

    List<Building> findAllByLocation(
        Double longitudeLower,
        Double longitudeUpper,
        Double latitudeLower,
        Double latitudeUpper
    );

}
