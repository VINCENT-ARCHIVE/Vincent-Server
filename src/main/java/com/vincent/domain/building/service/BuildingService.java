package com.vincent.domain.building.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.building.repository.SpaceRepository;
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
    private final FloorRepository floorRepository;
    private final SpaceRepository spaceRepository;
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
        String uploadUrl = s3Service.upload(image, "Building");
        building.setImage(uploadUrl);
        buildingRepository.save(building);
    }

    @Transactional
    public void createFloor(Long buildingId, int level, MultipartFile image) throws IOException {
        String uploadUrl = s3Service.upload(image, "Floor");
        Building building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));

        Floor floor = Floor.builder()
            .building(building)
            .level(level)
            .image(uploadUrl)
            .build();

        floorRepository.save(floor);
    }

    public List<Building> getBuildingLocation(Double longitude, Double latitude) {
        double longitudeRange = 3.1; //임의의 범위
        double latitudeRange = 6.1; //임의의 범위
        double longitudeLower = longitude - longitudeRange;
        double longitudeUpper = longitude + longitudeRange;
        double latitudeLower = latitude - latitudeRange;
        double latitudeUpper = latitude + latitudeRange;
        return buildingRepository.findAllByLocation(longitudeLower, longitudeUpper, latitudeLower, latitudeUpper);

    }

    @Transactional
    public void createSpace(Long floorId, MultipartFile image, int x, int y, String name) throws IOException {
        String uploadUrl = s3Service.upload(image, "Space");
        Floor floor = floorRepository.findById(floorId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.FLOOR_NOT_FOUND));

        Space space = Space.builder()
            .floor(floor)
            .name(name)
            .xCoordinate(x)
            .yCoordinate(y)
            .image(uploadUrl)
            .build();

        spaceRepository.save(space);


    }


}
