package com.vincent.domain.building.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.building.repository.SpaceRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SocketRepository socketRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private BuildingService buildingService;

    private Building building;
    private Floor floor;
    private Space space;

    private FloorInfoProjection floorInfoProjection;
    private SpaceInfoProjection spaceInfoProjection;
    private List<SpaceInfoProjection> spaceInfoProjectionList;


    @BeforeEach
    public void setUp() {
        building = Building.builder()
            .id(1L)
            .name("Test Building")
            .address("123 Test St.")
            .latitude(10.0)
            .longitude(20.0)
            .build();

        floor = Floor.builder()
            .id(1L)
            .building(building)
            .level(1)
            .build();

        space = Space.builder()
            .id(1L)
            .floor(floor)
            .name("Test Space")
            .xCoordinate(10.0)
            .yCoordinate(20.0)
            .build();

        floorInfoProjection = new FloorInfoProjection() {
            @Override
            public String getBuildingName() {
                return "Building1";
            }

            @Override
            public Integer getFloors() {
                return 5;
            }

            @Override
            public Integer getLevel() {
                return 2;
            }

            @Override
            public String getImage() {
                return "floorImage.jpg";
            }
        };


        spaceInfoProjection = new SpaceInfoProjection() {
            @Override
            public String getSpaceName() {
                return "Space1";
            }

            @Override
            public Double getxCoordinate() {
                return 1.1;
            }

            @Override
            public Double getyCoordinate() {
                return 1.1;
            }

            @Override
            public Boolean getIsSocketExist() {
                return true;
            }
        };

        spaceInfoProjectionList = List.of(spaceInfoProjection);
    }




    @Test
    public void 건물정보조회성공() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        Building result = buildingService.getBuildingInfo(1L);

        assertEquals(building, result);
        verify(buildingRepository, times(1)).findById(1L);
    }

    @Test
    public void 건물정보조회실패_건물없음() {

        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            buildingService.getBuildingInfo(1L);
        });

        assertEquals(ErrorStatus.BUILDING_NOT_FOUND, thrown.getCode());
        verify(buildingRepository, times(1)).findById(1L);
    }

    @Test
    public void 빌딩검색성공() {
        //given
        String keyword = "test";
        Page<Building> buildingPage = new PageImpl<>(List.of(building));

        //when
        when(buildingRepository.findByNameContainingOrderBySimilarity(keyword,
            PageRequest.of(0, 10))).thenReturn(buildingPage);

        //then
        Page<Building> result = buildingService.getBuildingSearch(keyword, 0);
        assertThat(result.getContent()).isEqualTo(buildingPage.getContent());
    }

    @Test
    public void 빌딩_등록_성공() throws IOException {
        //given
        MultipartFile image = mock(MultipartFile.class);
        String name = "building1";
        String address = "b1_address";
        double latitude = 10.2;
        double longitude = 10.9;
        String mockUrl = "test.com";

        //when
        when(s3Service.upload(image, "Building")).thenReturn(mockUrl);

        //then
        buildingService.createBuilding(image, name, address, latitude, longitude);

        ArgumentCaptor<Building> buildingCaptor = ArgumentCaptor.forClass(Building.class);
        verify(buildingRepository).save(buildingCaptor.capture());
        Building savedBuilding = buildingCaptor.getValue();

        // 검증: 저장된 빌딩 객체의 필드 값이 예상된 값과 일치하는지 확인
        assertThat(savedBuilding.getName()).isEqualTo("building1");
        assertThat(savedBuilding.getAddress()).isEqualTo("b1_address");
        assertThat(savedBuilding.getLatitude()).isEqualTo(10.2);
        assertThat(savedBuilding.getLongitude()).isEqualTo(10.9);
        assertThat(savedBuilding.getImage()).isEqualTo(mockUrl);
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
            buildingService.createFloor(1L, level, image);
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

        given(buildingRepository.findAllByLocation(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
            .willReturn(Collections.singletonList(building));

        List<Building> result = buildingService.getBuildingLocation(36.0, 120.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getLongitude()).isEqualTo(36.1);
        assertThat(result.get(0).getLatitude()).isEqualTo(120.1);
    }


    @Test
    public void 층_조회_getFloorInfo_성공() {

        Long buildingId = 1L;
        Integer level = 1;
        when(floorRepository.findFloorInfoByBuildingIdAndLevel(buildingId, level))
            .thenReturn(floorInfoProjection);


        FloorInfoProjection result = buildingService.getFloorInfo(buildingId, level);


        assertThat(result).isNotNull();
        assertThat(result.getBuildingName()).isEqualTo("Building1");
        assertThat(result.getFloors()).isEqualTo(5);
        assertThat(result.getLevel()).isEqualTo(2);
        assertThat(result.getImage()).isEqualTo("floorImage.jpg");
    }







    @Test
    public void 층_조회_getSpaceInfoList_성공() {
        Long buildingId = 1L;
        Integer level = 1;
        when(spaceRepository.findSpaceInfoByBuildingIdAndLevel(buildingId, level))
            .thenReturn(spaceInfoProjectionList);

        List<SpaceInfoProjection> result = buildingService.getSpaceInfoList(buildingId, level);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        SpaceInfoProjection spaceInfo = result.get(0);
        assertThat(spaceInfo.getSpaceName()).isEqualTo("Space1");
        assertThat(spaceInfo.getxCoordinate()).isEqualTo(1.1);
        assertThat(spaceInfo.getyCoordinate()).isEqualTo(1.1);
        assertThat(spaceInfo.getIsSocketExist()).isTrue();
    }






    @Test
    void createSpace_성공() throws IOException {

        MultipartFile image = Mockito.mock(MultipartFile.class);

        Long floorId = 1L;
        double xCoordinate = 10;
        double yCoordinate = 20;
        String name = "Space";
        boolean isSocketExist = true;

        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));

        buildingService.createSpace(floorId, xCoordinate, yCoordinate, name, isSocketExist);

        verify(spaceRepository).save(any(Space.class));
    }

    @Test
    void createSpace_실패_층없음() {

        Long floorId = 1L;
        MultipartFile image = null;
        boolean isSocketExist = true;

        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());


        assertThrows(ErrorHandler.class, () -> {
            buildingService.createSpace(floorId,10, 20, "Test Space", isSocketExist);
        });
    }

    @Test
    void 소켓등록_성공() throws IOException {

        MultipartFile image = Mockito.mock(MultipartFile.class);
        String mockUploadUrl = "http://mock-s3-url/socket.jpg";


        when(spaceRepository.findById(1L)).thenReturn(Optional.of(space));
        when(s3Service.upload(image, "Socket")).thenReturn(mockUploadUrl);


        buildingService.createSocket(1L, image, 123.45, 543.21, "Test Socket", 3);


        verify(spaceRepository).findById(1L);
        verify(s3Service).upload(image, "Socket");
        verify(socketRepository).save(any(Socket.class));
    }

}
