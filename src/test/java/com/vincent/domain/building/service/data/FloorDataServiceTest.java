package com.vincent.domain.building.service.data;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocket;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FloorDataServiceTest {
    @Mock
    private FloorRepository floorRepository;

    @InjectMocks
    private FloorDataService floorDataService;

    @Test
    void 아이디로_층_찾기_성공() {
        // given
        Long floorId = 1L;
        Floor floor = Floor.builder().id(1L).build();

        //when
        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));

        // when
        Floor result = floorDataService.findById(floorId);

        // then
        Assertions.assertEquals(result, floor);
        verify(floorRepository, times(1)).findById(floorId);
    }

    @Test
    void 아이디로_층_찾기_실패() {
        // given
        Long buildingId = 1L;
        //when
        when(floorRepository.findById(buildingId)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> floorDataService.findById(buildingId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.FLOOR_NOT_FOUND);
    }

    @Test
    void 층_저장() {
        //given
        Floor floor = Floor.builder().id(1L).build();

        //when
        when(floorRepository.save(floor)).thenReturn(floor);

        //then
        floorDataService.save(floor);
        verify(floorRepository,times(1)).save(floor);

    }

    @Test
    void 건물ID와_층수로_층정보_찾기() {
        //given
        Long buildingId = 1L;
        int level = 0;
        FloorInfoProjection floorInfoProjection = new FloorInfoProjection();

        //when
        when(floorRepository.findFloorInfoByBuildingIdAndLevel(buildingId,level)).thenReturn(floorInfoProjection);

        //then
        FloorInfoProjection result = floorDataService.findFloorInfoByBuildingIdAndLevel(
            buildingId, level);
        Assertions.assertEquals(result, floorInfoProjection);
        verify(floorRepository, times(1)).findFloorInfoByBuildingIdAndLevel(buildingId, level);

    }

    @Test
    void 건물_및_층수로_층_찾기_성공() {
        // given
        int level = 2;
        Building building = Building.builder().id(1L).build();
        Floor floor = Floor.builder().id(1L).building(building).level(2).build();

        //when
        when(floorRepository.findByBuildingAndLevel(building, level)).thenReturn(Optional.of(floor));

        // then
        Floor result = floorDataService.findByBuildingAndLevel(building, level);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(floor, result);
        verify(floorRepository, times(1)).findByBuildingAndLevel(building, level);
    }

    @Test
    void 건물_및_층수로_층_찾기_실패() {
        // given
        int level = 2;
        Building building = Building.builder().id(1L).build();

        //when
        when(floorRepository.findByBuildingAndLevel(building, level)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> floorDataService.findByBuildingAndLevel(building, level));
        Assertions.assertEquals(ErrorStatus.FLOOR_NOT_FOUND, thrown.getCode());
    }

    @Test
    public void 건물로_소켓이_있는_층_찾기_성공() {
        // given
        Long buildingId = 1L;
        List<FloorWithSocket> expectedFloorWithSocketList = List.of(new FloorWithSocket(/* 필요한 필드값 입력 */));

        // Mock 객체 반환값 설정
        when(floorRepository.findFloorWithSocketByBuildingId(buildingId)).thenReturn(expectedFloorWithSocketList);

        // when
        List<FloorWithSocket> result = floorDataService.findFloorWithSocketByBuildingId(buildingId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedFloorWithSocketList);
        verify(floorRepository, times(1)).findFloorWithSocketByBuildingId(buildingId); // 메서드 호출 확인
    }
}

