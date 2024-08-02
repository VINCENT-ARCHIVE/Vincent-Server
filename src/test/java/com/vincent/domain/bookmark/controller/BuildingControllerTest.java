package com.vincent.domain.bookmark.controller;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.controller.BuildingController;
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.service.BuildingService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
}