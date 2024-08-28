package com.vincent.domain.building.controller;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.controller.BuildingController;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.BuildingInfo;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.BuildingLocationList;
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.BuildingService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class BuildingControllerTest {

    @Mock
    private BuildingService buildingService;

    @InjectMocks
    private BuildingController buildingController;

    private Building building;
    private Floor floor;
    private Space space;

    @BeforeEach
    public void setUp() {
        building = Building.builder()
            .id(1L)
            .name("Building 1")
            .image("image1")
            .address("address1")
            .build();

        floor = Floor.builder()
            .id(1L)
            .level(1)
            .building(building)
            .build();

        space = Space.builder()
            .id(1L)
            .name("Space 1")
            .longitude(10)
            .latitude(20)
            .floor(floor)
            .build();
    }

    @Test
    public void 건물정보조회_성공() {
        //given
        Long buildingId = 1L;

        //when
        when(buildingService.getBuildingInfo(eq(buildingId))).thenReturn(building);

        //then
        ApiResponse<BuildingResponseDto.BuildingInfo> response = buildingController.buildingInfo(
            buildingId);
        BuildingResponseDto.BuildingInfo result = response.getResult();
        BuildingInfo expected = BuildingConverter.toBuildingInfoResponse(building);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
        Assertions.assertThat(result.getBuildingId()).isEqualTo(expected.getBuildingId());
    }

    @Test
    public void 빌딤검색성공() {
        //given
        String keyword = "test";
        Integer page = 0;
        Page<Building> buildingPage = new PageImpl<>(List.of(building));

        //when
        when(buildingService.getBuildingSearch(keyword, page)).thenReturn(buildingPage);

        //then
        ApiResponse<BuildingResponseDto.BuildingList> response = buildingController.buildingList(
            keyword, page);
        BuildingResponseDto.BuildingList result = response.getResult();
        BuildingResponseDto.BuildingList expected = BuildingConverter.toBuildingListResponse(
            buildingPage);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
        Assertions.assertThat(result.getBuildingInfos()).usingRecursiveComparison()
            .isEqualTo(expected.getBuildingInfos());
    }

    @Test
    public void 빌딩등록성공() throws IOException {
        // Given
        MultipartFile image = null;
        String name = "Test Building";
        String address = "seoul gangnam";
        double latitude = 10.0;
        double longitude = 20.0;

        // When/Then
        ApiResponse<?> response = buildingController.createBuilding(image, name, address, latitude,
            longitude);
        verify(buildingService).createBuilding(image, name, address, latitude, longitude);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();

    }

    @Test
    public void 층등록성공() throws IOException {
        // Given
        Long buildingId = 1L;
        MultipartFile image = null;
        int level = 1;

        // When/Then
        ApiResponse<?> response = buildingController.createFloor(buildingId, image, level);
        verify(buildingService).createFloor(buildingId, level, image);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();
    }

    @Test
    public void 공간등록성공() throws IOException {
        // Given
        Long floorId = 1L;
        MultipartFile image = null;
        String name = "test";
        double longitude = 10.0;
        double latitude = 20.0;
        boolean isSocketExist = true;

        //when/then
        ApiResponse<?> response = buildingController.createSpace(floorId, image, name, longitude, latitude, isSocketExist);
        verify(buildingService).createSpace(floorId, image, longitude, latitude, name, isSocketExist);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();
    }

    @Test
    public void 소켓등록성공() throws IOException {
        // Given
        Long spaceId = 1L;
        MultipartFile image = null;
        String name = "test";
        double longitude = 10.0;
        double latitude = 20.0;
        int holes = 3;

        //when/then
        ApiResponse<?> response = buildingController.createSocket(spaceId, image, name, longitude, latitude, holes);
        verify(buildingService).createSocket(spaceId, image, longitude, latitude, name, holes);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();
    }

    @Test
    public void 주변건물조회_성공() {
        //given
        Double latitude = 36.0;
        Double longitude = 120.0;

        List<Building> buildingList = List.of(building);
        //when
        when(buildingService.getBuildingLocation(longitude,latitude)).thenReturn(buildingList);

        //then
        ApiResponse<BuildingResponseDto.BuildingLocationList> response = buildingController.buildingLocation(
            latitude, longitude);
        BuildingResponseDto.BuildingLocationList result = response.getResult();
        BuildingLocationList expected = BuildingConverter.toBuildingLocationListResponse(
            buildingList);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
        Assertions.assertThat(result.getBuildingLocations()).usingRecursiveComparison()
            .isEqualTo(expected.getBuildingLocations());
    }

    /*
    @Test
    public void 층_조회_성공(){
        //given
        Long buildingId = 1L;
        Integer level = 1;

        List<Floor> floors = List.of(floor);
        List<Space> spaces = List.of(space);

        //when
        when(buildingService.getFloorInfo(buildingId, level)).thenReturn(floor);
        when(buildingService.getFloorInfoList(buildingId)).thenReturn(floors);
        when(buildingService.getSpaceInfoList(floor.getId())).thenReturn(spaces);

        //then
        ApiResponse<BuildingResponseDto.FloorInfoList> response = buildingController.floorInfoList(
            buildingId, level);
        BuildingResponseDto.FloorInfoList result = response.getResult();
        BuildingResponseDto.FloorInfoList expected = BuildingConverter.toFloorInfoListResponse(
            floor, floors, spaces);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
        Assertions.assertThat(result.getBuildingName()).isEqualTo(expected.getBuildingName());
    }

     */

}
