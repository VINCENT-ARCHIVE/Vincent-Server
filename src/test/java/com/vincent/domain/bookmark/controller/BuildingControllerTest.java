package com.vincent.domain.bookmark.controller;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.controller.BuildingController;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.service.BuildingService;
import com.vincent.exception.handler.ErrorHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        .andExpect(jsonPath("$.result").exists());;
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
        .andExpect(jsonPath("$.code").value(ErrorStatus.BUILDING_NOT_FOUND.getReason().getCode()))
        .andExpect(jsonPath("$.message").value(ErrorStatus.BUILDING_NOT_FOUND.getReason().getMessage()));
  }
}