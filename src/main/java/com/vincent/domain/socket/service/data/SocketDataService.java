package com.vincent.domain.socket.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketDataService {

    private final SocketRepository socketRepository;

    public Socket findById(Long id) {
        return socketRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));
    }

    public Socket findByUniqueId(String id) {
        return socketRepository.findByName(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));
    }

    public List<Socket> findAllBySpace(Space space) {
        return socketRepository.findAllBySpace(space);
    }

    public Socket save(Socket socket) {
        return socketRepository.save(socket);
    }

    public Socket findSocketPlaceBySocketId(Long socketId) {
        return socketRepository.findSocketPlaceBySocketId(socketId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));

    }

    public List<Socket> findSocketListByBuildingIdAndLevel(Long buildingId, Integer level) {
        return socketRepository.findSocketListByBuildingIdAndLevel(buildingId, level);
    }

}
