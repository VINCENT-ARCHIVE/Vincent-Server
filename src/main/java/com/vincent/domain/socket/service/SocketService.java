package com.vincent.domain.socket.service;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.data.BuildingDataService;
import com.vincent.domain.building.service.data.FloorDataService;
import com.vincent.domain.building.service.data.SpaceDataService;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocketService {

    private final SocketDataService socketDataService;

    public Socket getSocketInfo(Long socketId) {
        return socketDataService.findById(socketId);
    }

    public List<Socket> getSocketList(Long buildingId, int level) {

        return socketDataService.findSocketListByBuildingIdAndLevel(buildingId, level);
    }

    public Socket getSocketPlace(Long socketId) {
        return socketDataService.findSocketPlaceBySocketId(socketId);
    }

}
