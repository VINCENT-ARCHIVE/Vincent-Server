package com.vincent.domain.building.repository.customfloor;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocket;
import java.util.List;

public interface CustomFloorRepository {

    FloorInfoProjection findFloorInfoByBuildingIdAndLevel(Long buildingId, int level);

    List<FloorWithSocket>  findFloorWithSocketByBuildingId(Long buildingId);

}
