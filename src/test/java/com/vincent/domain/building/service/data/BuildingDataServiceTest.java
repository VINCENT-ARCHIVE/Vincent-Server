package com.vincent.domain.building.service.data;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.BuildingRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BuildingDataServiceTest {
    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private BuildingDataService buildingDataService;
    private static final Double LONGITUDE = 127.0;
    private static final Double LATITUDE = 37.0;

    @Test
    void 아이디로_빌딩_찾기_성공() {
        // given
        Long buildingId = 1L;
        Building building = Building.builder().id(1L).build();

        //when
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));

        // then
        Building result = buildingDataService.findById(buildingId);
        Assertions.assertEquals(result, building);
        verify(buildingRepository, times(1)).findById(buildingId);
    }

    @Test
    void 아이디로_빌딩_찾기_실패() {
        // given
        Long buildingId = 1L;
        //when
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> buildingDataService.findById(buildingId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.BUILDING_NOT_FOUND);
    }

    @Test
    void 빌딩_저장() {
        // given
        Building building = Building.builder().id(1L).build();

        //when
        when(buildingRepository.save(building)).thenReturn(building);

        //then
        buildingDataService.save(building);
        verify(buildingRepository, times(1)).save(building);
    }

    @Test
    void 위치로_빌딩_찾기() {
        // given
        Building building = Building.builder().id(1L).build();
        List<Building> buildingList = Arrays.asList(building);
        double longitudeRange = 0.011364;
        double latitudeRange = 0.008983;
        double longitudeLower = LONGITUDE - longitudeRange;
        double longitudeUpper = LONGITUDE + longitudeRange;
        double latitudeLower = LATITUDE - latitudeRange;
        double latitudeUpper = LATITUDE + latitudeRange;

        when(buildingRepository.findAllByLocation(longitudeLower, longitudeUpper, latitudeLower, latitudeUpper))
            .thenReturn(buildingList);

        // when
        List<Building> result = buildingDataService.findAllByLocation(LONGITUDE, LATITUDE);

        // then
        Assertions.assertEquals(result, buildingList);
        verify(buildingRepository, times(1)).findAllByLocation(longitudeLower, longitudeUpper, latitudeLower, latitudeUpper);
    }

    @Test
    void 이름으로_빌딩_찾기() {
        // given
        int page = 0;
        String keyword = "test";
        Building building = Building.builder().id(1L).build();
        Page<Building> pageResult = new PageImpl<>(Collections.singletonList(building));
        when(buildingRepository.findByNameContainingOrderBySimilarity(keyword, PageRequest.of(page, 10)))
            .thenReturn(pageResult);

        // when
        Page<Building> result = buildingDataService.findAllByName(keyword, page);

        // then
        verify(buildingRepository, times(1))
            .findByNameContainingOrderBySimilarity(keyword, PageRequest.of(page, 10));
    }
}
