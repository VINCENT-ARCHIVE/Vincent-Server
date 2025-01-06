package com.vincent.domain.building.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildingDataService {

    private final BuildingRepository buildingRepository;

    public Building findById(Long id) {
        return buildingRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));
    }

    public Building save(Building building) {
        return buildingRepository.save(building);
    }

    public List<Building> findAllByLocation(Double longitude, Double latitude) {
        final double longitudeRange = 0.011364;
        final double latitudeRange = 0.008983;

        double longitudeLower = longitude - longitudeRange;
        double longitudeUpper = longitude + longitudeRange;
        double latitudeLower = latitude - latitudeRange;
        double latitudeUpper = latitude + latitudeRange;

        return buildingRepository.findAllByLocation(longitudeLower, longitudeUpper, latitudeLower, latitudeUpper);
    }

    public Page<Building> findAllByName(String keyword, Integer page) {
        return buildingRepository.findByNameContainingOrderBySimilarity(keyword,
            PageRequest.of(page, 10));
    }

}
