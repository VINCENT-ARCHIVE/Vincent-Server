package com.vincent.domain.bookmark.controller;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.BuildingService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

@WebMvcTest(controllers = BuildingController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;

    Long buildingId = 1L;

    @MockBean
    private Building building;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    public void 건물정보조회_성공() throws Exception {
        given(buildingService.getBuildingInfo(eq(buildingId))).willReturn(building);

        ResultActions resultActions = mockMvc.perform(get("/v1/building/{buildingId}", buildingId)
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("$.message").value("성공입니다"))
            .andExpect(jsonPath("$.result").exists());
        ;
    }

    @Test
    @WithMockUser
    public void 건물정보조회실패_건물없음() throws Exception {
        given(buildingService.getBuildingInfo(eq(buildingId)))
            .willThrow(new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));

        ResultActions resultActions = mockMvc.perform(get("/v1/building/{buildingId}", buildingId)
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(
                ErrorStatus.BUILDING_NOT_FOUND.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(
                ErrorStatus.BUILDING_NOT_FOUND.getReason().getMessage()));
    }

    @Test
    @WithMockUser
    public void 빌딩검색성공() throws Exception {

        String keyword = "test";
        int page = 0;
        Building building1 = Mockito.mock(Building.class);
        when(building1.getId()).thenReturn(1L);
        when(building1.getName()).thenReturn("test1");
        when(building1.getImage()).thenReturn("image1");
        when(building1.getAddress()).thenReturn("address1");

        Building building2 = Mockito.mock(Building.class);
        when(building2.getId()).thenReturn(2L);
        when(building2.getName()).thenReturn("test2");
        when(building2.getImage()).thenReturn("image2");
        when(building2.getAddress()).thenReturn("address2");

        List<Building> buildings = Arrays.asList(building1, building2);
        Pageable pageable = PageRequest.of(page, 10);
        Page<Building> buildingPage = new PageImpl<>(buildings, pageable, buildings.size());

        when(buildingService.getBuildingSearch(keyword, page)).thenReturn(buildingPage);

        ResultActions resultActions = mockMvc.perform(get("/v1/building/search")
            .param("keyword", keyword)
            .param("page", String.valueOf(page))
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(
                ApiResponse.onSuccess(
                    BuildingConverter.toBuildingListResponse(buildingPage))
            )));
    }

    @Test
    @WithMockUser
    public void 주변건물조회_성공() throws Exception {

        BuildingResponseDto.BuildingLocation location = BuildingResponseDto.BuildingLocation.builder()
            .buildingId(1L)
            .longitude(36.1)
            .latitude(120.1)
            .build();

        BuildingResponseDto.BuildingLocationList locationList = BuildingResponseDto.BuildingLocationList.builder()
            .buildingLocations(Collections.singletonList(location))
            .build();

        given(buildingService.getBuildingLocation(36.0, 120.0)).willReturn(Collections.singletonList(
            Building.builder()
                .id(1L)
                .longitude(36.1)
                .latitude(120.1)
                .build()
        ));

        ResultActions resultActions = mockMvc.perform(get("/v1/building/location")
            .param("longitude", "36.0")
            .param("latitude", "120.0")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.result.buildingLocations[0].buildingId").value(1))
            .andExpect(jsonPath("$.result.buildingLocations[0].longitude").value(36.1))
            .andExpect(jsonPath("$.result.buildingLocations[0].latitude").value(120.1));
    }


    @Test
    @WithMockUser
    public void 층_조회_성공() throws Exception {
        Long buildingId = 1L;
        Integer level = 2;

        Building building = Mockito.mock(Building.class);

        Floor floor = Mockito.mock(Floor.class);
        when(floor.getLevel()).thenReturn(level);
        when(floor.getBuilding()).thenReturn(building);

        List<Floor> floors = new ArrayList<>();
        List<Space> spaces = new ArrayList<>();

        when(buildingService.getFloorInfo(buildingId, level)).thenReturn(floor);
        when(buildingService.getFloorInfoList(buildingId)).thenReturn(floors);
        when(buildingService.getSpaceInfoList(floor.getId())).thenReturn(spaces);

        BuildingResponseDto.FloorInfoList floorInfoList = BuildingResponseDto.FloorInfoList.builder()
            .buildingName("Building 1")
            .floors(3)
            .currentFloor(2)
            .spaceInfoList(new ArrayList<>())
            .build();

        when(BuildingConverter.toFloorInfoListResponse(floor, floors, spaces)).thenReturn(floorInfoList);

        mockMvc.perform(get("/v1/building/floor")
                .param("buildingId", buildingId.toString())
                .param("level", level.toString())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.buildingName").value("Building 1"));
    }

    @Test
    @WithMockUser
    public void 층_조회_실패_빌딩없음() throws Exception {
        Long buildingId = 1L;
        Integer level = 2;

        when(buildingService.getFloorInfo(buildingId, level)).thenThrow(
            new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND));

        mockMvc.perform(get("/v1/building/floor")
                .param("buildingId", buildingId.toString())
                .param("level", level.toString())
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 층등록_성공() throws Exception {

        Long floorId = 1L;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
        String name = "Test Space";
        int xCoordinate = 10;
        int yCoordinate = 20;


        mockMvc.perform(multipart("/v1/building/floors/{floorId}/spaces", floorId)
                .file(image)
                .param("xCoordinate", String.valueOf(xCoordinate))
                .param("yCoordinate", String.valueOf(yCoordinate))
                .param("name", name)
                .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().isOk());

        verify(buildingService).createSpace(floorId, image, xCoordinate, yCoordinate, name);
    }


}
