package com.vincent.domain.building.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorInfo {

        String buildingName;
        Integer floors;
        Integer currentFloor;
        String floorImage;
        List<SpaceInfoProjection> spaceInfoList;



    }



    public interface FloorInfoProjection {
        String getBuildingName();

        Integer getFloors();

        Integer getLevel();

        String getImage();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpaceInfo {

        String spaceName;
        Double xCoordinate;
        Double yCoordinate;
        Boolean socketExistence;
    }

    @JsonPropertyOrder({"spaceName", "xCoordinate", "yCoordinate", "isSocketExist"})
    public interface SpaceInfoProjection {
        String getSpaceName();

        Double getxCoordinate();

        Double getyCoordinate();

        Boolean getIsSocketExist();
    }


}
