package com.vincent.domain.building.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final S3Service s3Service;

    public Building getBuildingInfo(Long buildingId) {

        return buildingRepository.findById(buildingId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));
    }

    public Page<Building> getBuildingSearch(String keyword, Integer page) {

        return buildingRepository.findByNameContainingOrderBySimilarity(keyword,
            PageRequest.of(page, 10));
    }

    @Transactional
    public void createBuilding(Building building, MultipartFile image) throws IOException {
        String uploadUrl = s3Service.upload(image);
        building.setImage(uploadUrl);
        buildingRepository.save(building);
    }

    public List<Building> getBuildingLocation(Double longitude, Double latitude) {
        return buildingRepository.findAllByLocation(longitude, latitude);

    }


}
