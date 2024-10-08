package com.vincent.domain.building.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocket;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FloorDataService {

    private final FloorRepository floorRepository;

    public Floor findById(Long id) {
        return floorRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.FLOOR_NOT_FOUND));
    }

    public Floor save(Floor floor) {
        return floorRepository.save(floor);
    }

    public FloorInfoProjection findFloorInfoByBuildingIdAndLevel(Long buildingId, int level) {
        return floorRepository.findFloorInfoByBuildingIdAndLevel(buildingId, level);
    }

    public List<FloorWithSocket> findFloorWithSocketByBuildingId(Long buildingId) {
        return floorRepository.findFloorWithSocketByBuildingId(buildingId);
    }

    public Floor findByBuildingAndLevel(Building building, Integer level) {
        return floorRepository.findByBuildingAndLevel(building, level)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.FLOOR_NOT_FOUND));
    }
}
