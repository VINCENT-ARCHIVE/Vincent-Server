package com.vincent.domain.socket.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.building.repository.SpaceRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocketService {

    private final SocketRepository socketRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final SpaceRepository spaceRepository;

    public Socket getSocketInfo(Long socketId) {

        return  socketRepository.findById(socketId).orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));
    }

    public List<Socket> getSocketLocationList(Long buildingId, int level) {

        Building a = buildingRepository.findById(buildingId).orElseThrow(()
            -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));

        Floor b = floorRepository.findByBuildingAndLevel(a, level);

        List<Space> s = spaceRepository.findAllByFloor(b);

        List<Socket> socket = null;

        for (Space space : s) {

            socket = socketRepository.findAllBySpace(space);
        }


        return  socket;
    }

}
