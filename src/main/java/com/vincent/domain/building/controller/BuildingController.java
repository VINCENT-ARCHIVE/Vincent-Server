package com.vincent.domain.building.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/building")
    public ApiResponse<BuildingResponseDto.BuildingList> buildingList(
            @RequestParam("contents") String contents,
            @RequestParam("page") Integer page) {
        Page<Building> buildingPage = buildingService.getBuildingSearch(contents, page);
        return ApiResponse.onSuccess(BuildingConverter.toBuildingListResponse(buildingPage));
    }

}
