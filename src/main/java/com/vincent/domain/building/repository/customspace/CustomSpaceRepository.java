package com.vincent.domain.building.repository.customspace;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface CustomSpaceRepository {

    List<SpaceInfoProjection> findSpaceInfoByBuildingIdAndLevel(Long buildingId, int level);

}
