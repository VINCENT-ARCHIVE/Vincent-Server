package com.vincent.domain.socket.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SocketResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocketInfo {

        Long socketId;
        String socketName;
        String socketImage;
        String buildingName;
        String spaceName;
        Boolean isBookmarkExist;
        Boolean isSocketUsing;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPropertyOrder({"socketId", "xCoordinate", "yCoordinate", "isSocketUsing"})
    public static class SocketLocation {

        Long socketId;

        @JsonProperty("xcoordinate")
        Double xCoordinate;

        @JsonProperty("ycoordinate")
        Double yCoordinate;

        Boolean isSocketUsing;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocketLocationList {

        List<SocketLocation> locationList;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocketPlace {

        Long buildingId;
        Integer level;
    }

}
