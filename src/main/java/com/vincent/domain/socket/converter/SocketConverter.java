package com.vincent.domain.socket.converter;

import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.entity.Socket;

public class SocketConverter {

    public static SocketResponseDto.SocketInfo toSocketInfoResponse(Socket socketInfo, Boolean isBookmarkExist) {
        return SocketResponseDto.SocketInfo.builder()
            .socketId(socketInfo.getId())
            .socketName(socketInfo.getName())
            .socketImage(socketInfo.getImage())
            .buildingName(socketInfo.getSpace().getFloor().getBuilding().getName())
            .spaceName(socketInfo.getSpace().getName())
            .isBookmarkExist(isBookmarkExist)
            .build();
    }

}
