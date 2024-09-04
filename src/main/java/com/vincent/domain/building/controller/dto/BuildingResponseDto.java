package com.vincent.domain.building.controller.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BuildingResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingInfo {

        Integer buildingId;
        String buildingName;
        String buildingImage;
        String buildingAddress;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingList {

        List<BuildingInfo> buildingInfos;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingLocation {

        Long buildingId;
        Double latitude;
        Double longitude;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingLocationList {

        List<BuildingLocation> buildingLocations;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorInfoList {

        String buildingName;
        Integer floors;
        Integer currentFloor;
        List<SpaceInfo> spaceInfoList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpaceInfo {

        String spaceName;
        Double longitude;
        Double latitude;
        Boolean socketExistence;
    }



}
