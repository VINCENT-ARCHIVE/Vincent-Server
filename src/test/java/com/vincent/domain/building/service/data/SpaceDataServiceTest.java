package com.vincent.domain.building.service.data;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.SpaceRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpaceDataServiceTest {
    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private SpaceDataService spaceDataService;

    @Test
    void 공간_저장() {
        //given
        Space space = Space.builder().id(1L).build();

        //when
        when(spaceRepository.save(space)).thenReturn(space);

        //then
        spaceDataService.save(space);
        verify(spaceRepository, times(1)).save(space);
    }

    @Test
    void 아이디로_공간_찾기_성공() {
        //given
        Long spaceId = 1L;
        Space space = Space.builder().id(1L).build();

        //when
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        //then
        Space result = spaceDataService.findById(spaceId);
        Assertions.assertEquals(result, space);
        verify(spaceRepository, times(1)).findById(spaceId);
    }

    @Test
    void 아이디로_공간_찾기_실패() {
        //given
        Long spaceId = 1L;

        //when
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> spaceDataService.findById(spaceId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.SPACE_NOT_FOUND);
    }

    @Test
    void 건물ID와_층수로_공간정보_리스트_가져오기() {
        //given
        Long buildingId = 1L;
        int level = 0;
        List<SpaceInfoProjection> spaceInfoList = Arrays.asList(new SpaceInfoProjection());

        //when
        when(spaceRepository.findSpaceInfoByBuildingIdAndLevel(buildingId, level)).thenReturn(spaceInfoList);

        //then
        List<SpaceInfoProjection> result = spaceDataService.getSpaceInfoList(buildingId, level);
        Assertions.assertEquals(result, spaceInfoList);
        verify(spaceRepository, times(1)).findSpaceInfoByBuildingIdAndLevel(buildingId, level);
    }
}
