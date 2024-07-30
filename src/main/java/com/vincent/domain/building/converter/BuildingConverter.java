package com.vincent.domain.building.converter;

import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto.BookmarkDetail;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.BuildingInfo;
import com.vincent.domain.building.entity.Building;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class BuildingConverter {

    public static BuildingResponseDto.BuildingInfo toBuildingInfoResponse(Building result) {
        return BuildingResponseDto.BuildingInfo.builder()
            .buildingId(result.getId())
            .buildingName(result.getName())
            .buildingImage(result.getImage())
            .buildingAddress(result.getAddress())
            .build();
    }

    public static BuildingResponseDto.BuildingList toBuildingListResponse(
            Page<Building> result) {
        List<BuildingResponseDto.BuildingInfo> buildingInfoList = result.stream()
                .map(BuildingConverter::toBuildingInfoResponse).collect(Collectors.toList());
        return BuildingResponseDto.BuildingList.builder()
                .isFirst(result.isFirst())
                .isLast(result.isLast())
                .totalPage(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .listSize(buildingInfoList.size())
                .buildingInfos(buildingInfoList)
                .build();
    }

}
