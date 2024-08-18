package com.vincent.domain.building.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.building.controller.dto.BuildingRequestDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.BuildingService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/building/{buildingId}")
    public ApiResponse<BuildingResponseDto.BuildingInfo> buildingInfo(
        @PathVariable("buildingId") Long buildingId) {
        Building result = buildingService.getBuildingInfo(buildingId);
        return ApiResponse.onSuccess(BuildingConverter.toBuildingInfoResponse(result));
    }

    @GetMapping("/building/search")
    public ApiResponse<BuildingResponseDto.BuildingList> buildingList(
        @RequestParam("keyword") String keyword,
        @RequestParam("page") Integer page) {
        Page<Building> buildingPage = buildingService.getBuildingSearch(keyword, page);
        return ApiResponse.onSuccess(BuildingConverter.toBuildingListResponse(buildingPage));
    }

    @PostMapping(value = "/building", consumes = "multipart/form-data")
    public ApiResponse<?> createBuilding(
        @RequestPart(value = "image") MultipartFile image,
        @RequestParam("name") String name,
        @RequestParam("address") String address,
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude) throws IOException {
        buildingService.createBuilding(image, name, address, latitude, longitude);
        return ApiResponse.onSuccess(null);
    }


    @PostMapping(value = "/building/{buildingId}/floors", consumes = "multipart/form-data")
    public ApiResponse<?> createFloor(
        @PathVariable("buildingId") Long buildingId,
        @RequestPart("image") MultipartFile image,
        @RequestParam("level") int level)
        throws IOException {
        buildingService.createFloor(buildingId, level, image);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/building/location")
    public ApiResponse<BuildingResponseDto.BuildingLocationList> buildingLocation(
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude) {
        List<Building> buildingList = buildingService.getBuildingLocation(longitude, latitude);
        return ApiResponse.onSuccess(
            BuildingConverter.toBuildingLocationListResponse(buildingList));
    }

    @GetMapping("/building/floor")
    public ApiResponse<BuildingResponseDto.FloorInfoList> floorInfoList(
        @RequestParam("buildingId") Long buildingId,
        @RequestParam("level") Integer level) {
        Floor floor = buildingService.getFloorInfo(buildingId, level);
        List<Floor> floors = buildingService.getFloorInfoList(buildingId);
        List<Space> spaces = buildingService.getSpaceInfoList(floor.getId());

        return ApiResponse.onSuccess((
            BuildingConverter.toFloorInfoListResponse(floor, floors, spaces)));


    }

    @PostMapping(value = "/building/floors/{floorId}/spaces", consumes = "multipart/form-data")
    public ApiResponse<?> createSpace(
        @PathVariable("floorId") Long floorId,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @RequestParam("name") String name,
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude,
        @RequestParam("isSocketExist") boolean isSocketExist)
        throws IOException {
        buildingService.createSpace(floorId, image, latitude, longitude, name, isSocketExist);
        return ApiResponse.onSuccess(null);
    }


    @PostMapping(value = "/building/floors/spaces/{spaceId}/socket", consumes = "multipart/form-data")
    public ApiResponse<?> createSocket(
        @PathVariable("spaceId") Long spaceId,
        @RequestPart(value = "image") MultipartFile image,
        @RequestParam("name") String name,
        @RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude,
        @RequestParam("holes") int holes)
        throws IOException {
        buildingService.createSocket(spaceId, image, latitude, longitude, name, holes);
        return ApiResponse.onSuccess(null);
    }


}
