package com.vincent.domain.socket.controller.dto;

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

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocketLocation {

        Long socketId;
        Double xCoordinate;
        Double yCoordinate;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocketLocationList {

        List<SocketLocation> locationList;

    }

}
