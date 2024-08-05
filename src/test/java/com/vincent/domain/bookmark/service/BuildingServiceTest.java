package com.vincent.domain.bookmark.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.building.service.BuildingService;
import com.vincent.exception.handler.ErrorHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.assertThat;

public class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private S3Service s3Service;
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

    @Test
    public void 빌딩검색성공() {

        String keyword = "test";
        int page = 0;

        Building building1 = mock(Building.class);
        when(building1.getId()).thenReturn(1L);
        when(building1.getName()).thenReturn("test1");
        when(building1.getImage()).thenReturn("image1");
        when(building1.getAddress()).thenReturn("address1");

        Building building2 = mock(Building.class);
        when(building2.getId()).thenReturn(2L);
        when(building2.getName()).thenReturn("test2");
        when(building2.getImage()).thenReturn("image2");
        when(building2.getAddress()).thenReturn("address2");

        List<Building> buildings = Arrays.asList(building1, building2);
        Pageable pageable = PageRequest.of(page, 10);
        Page<Building> buildingPage = new PageImpl<>(buildings, pageable, buildings.size());

        when(buildingRepository.findByNameContainingOrderBySimilarity(keyword,
            PageRequest.of(page, 10))).thenReturn(buildingPage);

        Page<Building> result = buildingService.getBuildingSearch(keyword, page);

        assertEquals(buildingPage, result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(buildings, result.getContent());
    }

    @Test
    public void 빌딩_등록_성공() throws IOException {
        //given
        MultipartFile image = mock(MultipartFile.class);
        Building building = Building.builder().build();
        String mockUrl = "test.com";

        //when
        when(s3Service.upload(image, "Building")).thenReturn(mockUrl);

        //then
        buildingService.createBuilding(building, image);
        verify(s3Service, times(1)).upload(eq(image), eq("Building"));
        verify(buildingRepository, times(1)).save(eq(building));
        assertEquals(mockUrl, building.getImage());
    }

    @Test
    public void 층_등록_성공() throws IOException {
        //given
        MultipartFile image = mock(MultipartFile.class);
        Long buildId = 1L;
        Building building = Building.builder().build();
        Floor floor = Floor.builder().build();
        int level = 1;
        String mockUrl = "test.com";

        //when
        when(s3Service.upload(image, "Floor")).thenReturn(mockUrl);
        when(buildingRepository.findById(buildId)).thenReturn(Optional.of(building));

        //then
        buildingService.createFloor(buildId, level, image);
        ArgumentCaptor<Floor> floorCaptor = ArgumentCaptor.forClass(Floor.class);
        verify(floorRepository).save(floorCaptor.capture());

        Floor savedFloor = floorCaptor.getValue();

        assertEquals(mockUrl, savedFloor.getImage());
        assertEquals(building, savedFloor.getBuilding());
        assertEquals(level, savedFloor.getLevel());
        verify(s3Service, times(1)).upload(eq(image), eq("Floor"));
    }

    @Test
    public void 층_등록_실패() throws IOException {
        //given
        MultipartFile image = mock(MultipartFile.class);
        Long buildId = 1L;
        int level = 1;
        String mockUrl = "test.com";

        //when
        when(s3Service.upload(image, "Floor")).thenReturn(mockUrl);
        when(buildingRepository.findById(buildId)).thenReturn(Optional.empty());

        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class, () -> {
            buildingService.createFloor(buildingId, level, image);
        });
        Assertions.assertEquals(ErrorStatus.BUILDING_NOT_FOUND, thrown.getCode());
    }

    @Test
    public void 주변건물조회_성공() {

        Building building = Building.builder()
            .id(1L)
            .longitude(36.1)
            .latitude(120.1)
            .build();

        given(buildingRepository.findAllByLocation(32.900, 39.100, 113.900, 126.100))
            .willReturn(Collections.singletonList(building));

        List<Building> result = buildingService.getBuildingLocation(36.0, 120.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getLongitude()).isEqualTo(36.1);
        assertThat(result.get(0).getLatitude()).isEqualTo(120.1);
    }

}
