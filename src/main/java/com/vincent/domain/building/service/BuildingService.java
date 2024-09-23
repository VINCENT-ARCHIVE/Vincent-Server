package com.vincent.domain.building.service;

import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.service.data.BuildingDataService;
import com.vincent.domain.building.service.data.FloorDataService;
import com.vincent.domain.building.service.data.SpaceDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {

    private final S3Service s3Service;
    private final BuildingDataService buildingDataService;
    private final FloorDataService floorDataService;
    private final SpaceDataService spaceDataService;
    private final SocketDataService socketDataService;

    public Building getBuildingInfo(Long buildingId) {
        return buildingDataService.findById(buildingId);
    }

    public Page<Building> getBuildingSearch(String keyword, Integer page) {
        return buildingDataService.findAllByName(keyword, page);
    }

    @Transactional
    public void createBuilding(
        MultipartFile image, String name, String address, double latitude, double longitude)
        throws IOException {
        String uploadUrl = s3Service.upload(image, "Building");

        Building building = Building.builder()
            .name(name)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .image(uploadUrl)
            .build();

        buildingDataService.save(building);
    }

    @Transactional
    public void createFloor(Long buildingId, int level, MultipartFile image) throws IOException {
        String uploadUrl = s3Service.upload(image, "Floor");
        Building building = buildingDataService.findById(buildingId);

        Floor floor = Floor.builder()
            .building(building)
            .level(level)
            .image(uploadUrl)
            .build();

        floorDataService.save(floor);
    }

    public List<Building> getBuildingLocation(Double longitude, Double latitude) {
        return buildingDataService.findAllByLocation(longitude, latitude);
    }

    public FloorInfoProjection getFloorInfo(Long buildingId, int level) {
        return floorDataService.findFloorInfoByBuildingIdAndLevel(buildingId, level);
    }

    public List<SpaceInfoProjection> getSpaceInfoList(Long buildingId, int level) {
        return spaceDataService.getSpaceInfoList(buildingId, level);
    }

    @Transactional
    public void createSpace(
        Long floorId, MultipartFile image, double yCoordinate, double xCoordinate, String name,
        boolean isSocketExist)
        throws IOException {
        String uploadUrl = s3Service.upload(image, "Space");
        Floor floor = floorDataService.findById(floorId);

        Space space = Space.builder()
            .floor(floor)
            .name(name)
            .xCoordinate(xCoordinate)
            .yCoordinate(yCoordinate)
            .image(uploadUrl)
            .isSocketExist(isSocketExist)
            .build();

        spaceDataService.save(space);
    }

    @Transactional
    public void createSocket(
        Long spaceId, MultipartFile image, double yCoordinate, double xCoordinate, String name,
        int holes)
        throws IOException {
        String uploadUrl = s3Service.upload(image, "Socket");
        Space space = spaceDataService.findById(spaceId);

        space.setSocketExist(true);

        Socket socket = Socket.builder()
            .space(space)
            .name(name)
            .yCoordinate(yCoordinate)
            .xCoordinate(xCoordinate)
            .image(uploadUrl)
            .holes(holes)
            .build();

        socketDataService.save(socket);
    }


}
