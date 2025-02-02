package com.vincent.domain.socket.converter;

import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.entity.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class SocketConverter {

    public static SocketResponseDto.SocketInfo toSocketInfoResponse(Socket socketInfo, Boolean isBookmarkExist) {
        return SocketResponseDto.SocketInfo.builder()
            .socketId(socketInfo.getId())
            .socketName(socketInfo.getName())
            .socketImage(socketInfo.getImage())
            .buildingName(socketInfo.getSpace().getFloor().getBuilding().getName())
            .spaceName(socketInfo.getSpace().getName())
            .isBookmarkExist(isBookmarkExist)
            .isSocketUsing(socketInfo.getIsUsing())
            .build();
    }

    public static SocketResponseDto.SocketLocation toSocketLocation(Socket socket) {
        return SocketResponseDto.SocketLocation.builder()
            .socketId(socket.getId())
            .xCoordinate(socket.getXCoordinate())
            .yCoordinate(socket.getYCoordinate())
            .isSocketUsing(socket.getIsUsing())
            .build();
    }

    public static SocketResponseDto.SocketLocationList toSocketLocationList(List<Socket> socketList) {
        List<SocketResponseDto.SocketLocation> socketLocationList = socketList.stream()
            .map(SocketConverter::toSocketLocation).collect(Collectors.toList());
        return SocketResponseDto.SocketLocationList.builder()
            .locationList(socketLocationList)
            .build();
    }

    public static SocketResponseDto.SocketPlace toSocketPlace(Socket socket) {
        return SocketResponseDto.SocketPlace.builder()
            .buildingId(socket.getSpace().getFloor().getBuilding().getId())
            .level(socket.getSpace().getFloor().getLevel())
            .build();
    }

}
