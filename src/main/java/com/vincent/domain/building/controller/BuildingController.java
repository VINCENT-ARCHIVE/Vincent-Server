package com.vincent.domain.building.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.building.controller.dto.BuildingRequestDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfo;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocket;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocketProjection;
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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


    @Operation(summary = "건물의 정보 조회하기", description = "지도 상에서 건물 마크를 클릭했을 때 보여지는 정보를 제공함")
    @Parameter(name = "buildingId", description = "정보를 조회할 빌딩의 Id")
    @GetMapping("/building/{buildingId}")
    public ApiResponse<BuildingResponseDto.BuildingInfo> buildingInfo(
        @PathVariable("buildingId") Long buildingId) {
        Building result = buildingService.getBuildingInfo(buildingId);
        return ApiResponse.onSuccess(BuildingConverter.toBuildingInfoResponse(result));
    }

    @Operation(summary = "검색하기", description = "검색창에 입력한 키워드와 건물의 이름이 일치하는 순서대로 건물 목록을 조회함(한 페이지에 최대 10개씩)")
    @Parameter(name = "keyword", description = "검색 키워드")
    @Parameter(name = "page", description = "검색 목록의 페이지 번호(0부터 시작)")
    @GetMapping("/building/search")
    public ApiResponse<BuildingResponseDto.BuildingList> buildingList(
        @RequestParam("keyword") String keyword,
        @RequestParam("page") Integer page) {
        Page<Building> buildingPage = buildingService.getBuildingSearch(keyword, page);
        return ApiResponse.onSuccess(BuildingConverter.toBuildingListResponse(buildingPage));
    }

    @Operation(summary = "빌딩 등록하기")
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

    @Operation(summary = "층 등록하기")
    @Parameter(name = "level", description = "등록하려는 현재 층의 층수")
    @PostMapping(value = "/building/{buildingId}/floors", consumes = "multipart/form-data")
    public ApiResponse<?> createFloor(
        @PathVariable("buildingId") Long buildingId,
        @RequestPart("image") MultipartFile image,
        @RequestParam("level") int level)
        throws IOException {
        buildingService.createFloor(buildingId, level, image);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "주변 건물 조회하기",
        description = "지도의 중심 좌표를 기준으로 경도: 약 +-0.0007, 위도: 약 +-0.0014 범위 내에 있는 건물들의 위치를 조회함")
    @Parameter(name = "longitude", description = "지도의 중심 경도, 지도의 중심 x좌표")
    @Parameter(name = "latitude", description = "지도의 중심 위도, 지도의 중심 y좌표")
    @GetMapping("/building/location")
    public ApiResponse<BuildingResponseDto.BuildingLocationList> buildingLocation(
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude) {
        List<Building> buildingList = buildingService.getBuildingLocation(longitude, latitude);
        return ApiResponse.onSuccess(
            BuildingConverter.toBuildingLocationListResponse(buildingList));
    }


    @Operation(summary = "층 정보 조회하기", description = "빌딩의 총 층 수와 현재 층, 현재 층의 공간 정보들을 조회함")
    @Parameter(name = "level", description = "공간 정보를 조회하고 싶은 층")
    @GetMapping("/building/floor")
    public ApiResponse<BuildingResponseDto.FloorInfo> floorInfoList(
        @RequestParam("buildingId") Long buildingId,
        @RequestParam("level") Integer level) {
        FloorInfoProjection floorInfoProjection = buildingService.getFloorInfo(buildingId, level);
        List<FloorWithSocketProjection> floorWithSocketList = buildingService.getFloorList(buildingId);
        List<SpaceInfoProjection> spaceInfoProjectionList = buildingService.getSpaceInfoList(buildingId, level);
        return  ApiResponse.onSuccess((
            BuildingConverter.toFloorInfoListResponse(floorInfoProjection, floorWithSocketList, spaceInfoProjectionList)));


    }


    @Operation(summary = "공간 등록하기")
    @PostMapping(value = "/building/floors/{floorId}/spaces")
    public ApiResponse<?> createSpace(
        @PathVariable("floorId") Long floorId,
        @RequestParam("name") String name,
        @RequestParam("yCoordinate") Double yCoordinate,
        @RequestParam("xCoordinate") Double xCoordinate,
        @RequestParam("isSocketExist") boolean isSocketExist)
        throws IOException {
        buildingService.createSpace(floorId, yCoordinate, xCoordinate, name, isSocketExist);
        return ApiResponse.onSuccess(null);
    }



    @Operation(summary = "소켓 등록하기")
    @PostMapping(value = "/building/floors/spaces/{spaceId}/socket", consumes = "multipart/form-data")
    public ApiResponse<?> createSocket(
        @PathVariable("spaceId") Long spaceId,
        @RequestPart(value = "image") MultipartFile image,
        @RequestParam("name") String name,
        @RequestParam("yCoordinate") double yCoordinate,
        @RequestParam("xCoordinate") double xCoordinate,
        @RequestParam("holes") int holes)
        throws IOException {
        buildingService.createSocket(spaceId, image, yCoordinate, xCoordinate, name, holes);
        return ApiResponse.onSuccess(null);
    }




}
