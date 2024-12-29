package com.vincent.domain.socket.repository.customsocket;

import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import java.util.List;

public interface CustomSocketRepository {

    SocketPlace findSocketPlaceBySocketId(Long socketId);

    List<Socket> findSocketListByBuildingIdAndLevel(Long buildingId, Integer level);

}
