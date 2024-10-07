package com.vincent.domain.building.repository.customfloor;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;

public interface CustomFloorRepository {

    FloorInfoProjection findFloorInfoByBuildingIdAndLevel(Long buildingId, int level);

}
