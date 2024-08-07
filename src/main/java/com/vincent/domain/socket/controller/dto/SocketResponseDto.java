package com.vincent.domain.socket.controller.dto;

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

}
