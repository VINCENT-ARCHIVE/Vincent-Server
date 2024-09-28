package com.vincent.domain.building.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.config.aws.s3.S3Service;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.data.BuildingDataService;
import com.vincent.domain.building.service.data.FloorDataService;
import com.vincent.domain.building.service.data.SpaceDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BuildingServiceTest {
    @Mock
    private S3Service s3Service;

    @Mock
    private BuildingDataService buildingDataService;

    @Mock
    private FloorDataService floorDataService;

    @Mock
    private SpaceDataService spaceDataService;

    @Mock
    private SocketDataService socketDataService;

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

        floorInfoProjection = FloorInfoProjection.builder()
            .buildingName("Building1")
            .floors(5L)
            .currentFloor(2)
            .floorImage("floorImage.jpg")
            .build();


        spaceInfoProjection = SpaceInfoProjection.builder()
            .spaceName("Space1")
            .xCoordinate(1.1)
            .yCoordinate(1.1)
            .socketExistence(true)
            .build();


        spaceInfoProjectionList = List.of(spaceInfoProjection);
    }



    @Test
    public void 건물정보조회성공() {
        //given
        Long buildingId = 1L;
        Building building = Building.builder().id(1L).build();

        //when
        when(buildingDataService.findById(buildingId)).thenReturn(building);

        //then
        Building result = buildingService.getBuildingInfo(1L);

        assertEquals(building, result);
        verify(buildingDataService, times(1)).findById(buildingId);
    }


    @Test
    public void 빌딩검색성공() {
        //given
        String keyword = "test";
        Page<Building> buildingPage = new PageImpl<>(List.of(building));

        //when
        when(buildingDataService.findAllByName(keyword,0)).thenReturn(buildingPage);

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
        verify(buildingDataService).save(buildingCaptor.capture());
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
        when(buildingDataService.findById(buildId)).thenReturn(building);

        //then
        buildingService.createFloor(buildId, level, image);
        ArgumentCaptor<Floor> floorCaptor = ArgumentCaptor.forClass(Floor.class);
        verify(floorDataService).save(floorCaptor.capture());

        Floor savedFloor = floorCaptor.getValue();

        assertEquals(mockUrl, savedFloor.getImage());
        assertEquals(building, savedFloor.getBuilding());
        assertEquals(level, savedFloor.getLevel());
        verify(s3Service, times(1)).upload(eq(image), eq("Floor"));
    }


    @Test
    public void 주변건물조회_성공() {

        Building building = Building.builder()
            .id(1L)
            .longitude(36.1)
            .latitude(120.1)
            .build();

        when(buildingDataService.findAllByLocation(anyDouble(), anyDouble())).thenReturn(
            Collections.singletonList(building));

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
        when(floorDataService.findFloorInfoByBuildingIdAndLevel(buildingId, level)).thenReturn(
            floorInfoProjection);

        FloorInfoProjection result = buildingService.getFloorInfo(buildingId, level);


        assertThat(result).isNotNull();
        assertThat(result.getBuildingName()).isEqualTo("Building1");
        assertThat(result.getFloors()).isEqualTo(5);
        assertThat(result.getCurrentFloor()).isEqualTo(2);
        assertThat(result.getFloorImage()).isEqualTo("floorImage.jpg");
    }


    @Test
    public void 층_조회_getSpaceInfoList_성공() {
        Long buildingId = 1L;
        Integer level = 1;
        when(spaceDataService.getSpaceInfoList(buildingId,level)).thenReturn(spaceInfoProjectionList);

        List<SpaceInfoProjection> result = buildingService.getSpaceInfoList(buildingId, level);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        SpaceInfoProjection spaceInfo = result.get(0);
        assertThat(spaceInfo.getSpaceName()).isEqualTo("Space1");
        assertThat(spaceInfo.getXCoordinate()).isEqualTo(1.1);
        assertThat(spaceInfo.getYCoordinate()).isEqualTo(1.1);
        assertThat(spaceInfo.getSocketExistence()).isTrue();
    }


    @Test
    void createSpace_성공() throws IOException {
        //given
        MultipartFile image = Mockito.mock(MultipartFile.class);

        Long floorId = 1L;
        double xCoordinate = 10;
        double yCoordinate = 20;
        String name = "Space";
        String uploadUrl = "https://s3.amazonaws.com/example.jpg";
        boolean isSocketExist = true;

        //when
        when(floorDataService.findById(floorId)).thenReturn(floor);
        when(s3Service.upload(image, name)).thenReturn(uploadUrl);

        //then
        buildingService.createSpace(floorId, image, yCoordinate, xCoordinate, name, isSocketExist);

        verify(spaceDataService).save(any(Space.class));
        verify(s3Service).upload(image, "Space");
    }


    @Test
    void 소켓등록_성공() throws IOException {

        MultipartFile image = Mockito.mock(MultipartFile.class);
        String mockUploadUrl = "http://mock-s3-url/socket.jpg";


        when(spaceDataService.findById(1L)).thenReturn(space);
        when(s3Service.upload(image, "Socket")).thenReturn(mockUploadUrl);


        buildingService.createSocket(1L, image, 123.45, 543.21, "Test Socket", 3);


        verify(spaceDataService).findById(1L);
        verify(s3Service).upload(image, "Socket");
        verify(socketDataService).save(any(Socket.class));
    }

}
