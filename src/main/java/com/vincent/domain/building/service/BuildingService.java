package com.vincent.domain.building.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.building.repository.SpaceRepository;
import com.vincent.domain.member.entity.Member;
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

    private static final double longitudeRange = 0.0007092929741;
    private static final double latitudeRange = 0.00146597950179;

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

        double longitudeLower = longitude - longitudeRange;
        double longitudeUpper = longitude + longitudeRange;
        double latitudeLower = latitude - latitudeRange;
        double latitudeUpper = latitude + latitudeRange;

        return buildingRepository.findAllByLocation(longitudeLower, longitudeUpper, latitudeLower, latitudeUpper);

    }

    public Floor getFloorInfo(Long buildingId, Integer level) {

        Building building = findBuildingById(buildingId);

        return floorRepository.findByBuildingAndLevel(building, level);

    }

    public List<Floor> getFloorInfoList(Long buildingId) {

        Building building = findBuildingById(buildingId);

        return floorRepository.findAllByBuilding(building);

    }

    public List<Space> getSpaceInfoList(Long floorId) {

        Floor floor = findFloorById(floorId);

        return spaceRepository.findAllByFloor(floor);

    }

    private Building findBuildingById(Long buildingId) {
        return buildingRepository.findById(buildingId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));
    }

    private Floor findFloorById(Long floorId) {
        return floorRepository.findById(floorId).orElseThrow(() -> new ErrorHandler(ErrorStatus.FLOOR_NOT_FOUND));
    }

    @Transactional
    public void createSpace(
        Long floorId, MultipartFile image, double x, double y, String name, boolean isSocketExist) throws IOException {
        String uploadUrl = s3Service.upload(image, "Space");
        Floor floor = floorRepository.findById(floorId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.FLOOR_NOT_FOUND));

        Space space = Space.builder()
            .floor(floor)
            .name(name)
            .xCoordinate(x)
            .yCoordinate(y)
            .image(uploadUrl)
            .isSocketExist(isSocketExist)
            .build();

        spaceRepository.save(space);


    }


}
