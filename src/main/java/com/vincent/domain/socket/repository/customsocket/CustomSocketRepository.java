package com.vincent.domain.socket.repository.customsocket;

import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;

public interface CustomSocketRepository {

    SocketPlace findSocketPlaceBySocketId(Long socketId);

}
