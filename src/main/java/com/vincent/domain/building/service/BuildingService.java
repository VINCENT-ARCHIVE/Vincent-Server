package com.vincent.domain.building.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public Building getBuildingInfo(Long buildingId) {

        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));
    }

    public Page<Building> getBuildingSearch(String contents, Integer page) {

        return buildingRepository.findByNameContainingOrderBySimilarity(contents,
                PageRequest.of(page, 10));
    }


}
