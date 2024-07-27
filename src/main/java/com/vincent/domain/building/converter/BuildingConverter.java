package com.vincent.domain.building.converter;

import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.entity.Building;

public class BuildingConverter {

    public static BuildingResponseDto.BuildingInfo toBuildingInfoResponse(Building result) {
        return BuildingResponseDto.BuildingInfo.builder()
                .buildingId(result.getId())
                .buildingName(result.getName())
                .buildingImage(result.getImage())
                .buildingAddress(result.getAddress())
                .build();
    }

}
