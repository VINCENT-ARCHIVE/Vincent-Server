package com.vincent.domain.bookmark.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.service.BuildingService;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BuildingServiceTest {

  @Mock
  private BuildingRepository buildingRepository;

  @InjectMocks
  private BuildingService buildingService;

  Long buildingId = 1L;

  @Mock
  private Building building;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }



  @Test
  public void 건물정보조회성공() {

    given(buildingRepository.findById(buildingId)).willReturn(Optional.of(building));


    Building result = buildingService.getBuildingInfo(buildingId);


    assertEquals(building, result);
    verify(buildingRepository, times(1)).findById(buildingId);
  }

  @Test
  public void 건물정보조회실패_건물없음() {

    given(buildingRepository.findById(buildingId)).willReturn(Optional.empty());

    ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
      buildingService.getBuildingInfo(buildingId);
    });

    assertEquals(ErrorStatus.BUILDING_NOT_FOUND, thrown.getCode());
    verify(buildingRepository, times(1)).findById(buildingId);
  }

}
