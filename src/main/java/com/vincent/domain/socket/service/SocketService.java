package com.vincent.domain.socket.service;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.data.BuildingDataService;
import com.vincent.domain.building.service.data.FloorDataService;
import com.vincent.domain.building.service.data.SpaceDataService;
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
    private final BuildingDataService buildingDataService;
    private final FloorDataService floorDataService;
    private final SpaceDataService spaceDataService;

    public Socket getSocketInfo(Long socketId) {
        return socketDataService.findById(socketId);
    }

    public List<Socket> getSocketList(Long buildingId, int level) {

        Building building = buildingDataService.findById(buildingId);
        Floor floor = floorDataService.findByBuildingAndLevel(building, level);
        List<Space> spaces = spaceDataService.findAllByFloor(floor);

        return spaces.stream()
            .map(space -> socketDataService.findAllBySpace(space))
            .flatMap(sockets -> sockets.stream())
            .collect(Collectors.toList());

    }

}
