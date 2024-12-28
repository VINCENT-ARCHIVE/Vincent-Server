package com.vincent.domain.socket.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.service.data.BuildingDataService;
import com.vincent.domain.building.service.data.FloorDataService;
import com.vincent.domain.building.service.data.SpaceDataService;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SocketServiceTest {

    @Mock
    private FloorDataService floorDataService;

    @Mock
    private SpaceDataService spaceDataService;

    @Mock
    private BuildingDataService buildingDataService;

    @Mock
    private SocketDataService socketDataService;

    @InjectMocks
    private SocketService socketService;


    @Test
    public void 개별_콘센트_조회_성공() {
        //given
        Long socketId = 1L;
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(socketDataService.findById(socketId)).thenReturn(socket);

        //then
        Socket result = socketService.getSocketInfo(socketId);

        assertEquals(socket, result);
        verify(socketDataService, times(1)).findById(socketId);
    }


    @Test
    void 층소켓조회_성공() {
        //given
        Long buildingId = 1L;
        int level = 2;

        Building building = Building.builder().id(buildingId).build();
        Floor floor = Floor.builder().id(1L).level(level).building(building).build();
        Space space1 = Space.builder().id(1L).floor(floor).build();
        Space space2 = Space.builder().id(2L).floor(floor).build();
        Socket socket1 = Socket.builder().id(1L).space(space1).build();
        Socket socket2 = Socket.builder().id(2L).space(space2).build();
        List<Socket> expectedSockets = Arrays.asList(socket1, socket2);

        //when
        when(socketDataService.findSocketListByBuildingIdAndLevel(
            buildingId, level)).thenReturn(expectedSockets);

        //then
        List<Socket> result = socketService.getSocketList(buildingId, level);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(expectedSockets, result);
    }

    @Test
    void 소켓장소조회_성공() {

        //given
        Long socketId = 1L;
        SocketResponseDto.SocketPlace socketPlace;
        socketPlace = SocketPlace.builder().buildingId(1L).level(1).build();

        //when
        when(socketDataService.findSocketPlaceBySocketId(socketId)).thenReturn(socketPlace);

        //then
        SocketPlace result = socketService.getSocketPlace(socketId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(socketPlace, result);
        verify(socketDataService).findSocketPlaceBySocketId(socketId);
    }
}
