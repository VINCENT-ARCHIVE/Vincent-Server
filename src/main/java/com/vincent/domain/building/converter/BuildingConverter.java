package com.vincent.domain.building.converter;

import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto.BookmarkDetail;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.building.controller.dto.BuildingRequestDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.BuildingInfo;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
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

    public static Building toBuilding(BuildingRequestDto.Create create) {
        return Building.builder()
            .name(create.getName())
            .address(create.getAddress())
            .longitude(create.getLongitude())
            .latitude(create.getLatitude())
            .build();
    }

    public static BuildingResponseDto.BuildingLocation toBuildingLocationResponse(
        Building result) {
        return BuildingResponseDto.BuildingLocation.builder()
            .buildingId(result.getId())
            .latitude(result.getLatitude())
            .longitude(result.getLongitude())
            .build();
    }

    public static BuildingResponseDto.BuildingLocationList toBuildingLocationListResponse(
        List<Building> result) {
        List<BuildingResponseDto.BuildingLocation> b = result.stream()
            .map(BuildingConverter::toBuildingLocationResponse).collect(Collectors.toList());
        return BuildingResponseDto.BuildingLocationList.builder()
            .buildingLocations(b)
            .build();
    }

    public static BuildingResponseDto.SpaceInfo toSpaceInfoResponse(
        Space space) {
        return BuildingResponseDto.SpaceInfo.builder()
            .spaceName(space.getName())
            .xCoordinate(space.getXCoordinate())
            .yCoordinate(space.getYCoordinate())
            .socketExistence(space.isSocketExist()).build();

    }

    public static BuildingResponseDto.FloorInfoList toFloorInfoListResponse(
        Floor floor, List<Floor> floors, List<Space> spaces) {

        List<BuildingResponseDto.SpaceInfo> spaceInfoList = spaces.stream()
            .map(BuildingConverter::toSpaceInfoResponse).collect(Collectors.toList());

        return  BuildingResponseDto.FloorInfoList.builder()
            .buildingName(floor.getBuilding().getName())
            .floors(floors.size())
            .currentFloor(floor.getLevel())
            .spaceInfoList(spaceInfoList)
            .build();
    }



}
