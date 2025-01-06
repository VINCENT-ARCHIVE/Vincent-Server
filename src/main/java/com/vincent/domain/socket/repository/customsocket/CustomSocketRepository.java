package com.vincent.domain.socket.repository.customsocket;

import com.vincent.domain.socket.entity.Socket;
import java.util.List;
import java.util.Optional;

public interface CustomSocketRepository {

    Optional<Socket> findSocketPlaceBySocketId(Long socketId);

    List<Socket> findSocketListByBuildingIdAndLevel(Long buildingId, Integer level);

}
