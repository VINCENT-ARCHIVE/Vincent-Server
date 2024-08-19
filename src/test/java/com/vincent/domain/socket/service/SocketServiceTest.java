package com.vincent.domain.socket.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.building.repository.SpaceRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.domain.socket.service.SocketService;
import com.vincent.exception.handler.ErrorHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import org.mockito.Spy;

public class SocketServiceTest {

    @Mock
    private SocketRepository socketRepository;

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private SocketService socketService;

    @Spy
    private Socket socket;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 개별_콘센트_조회_성공() {

        Long socketId = 1L;

        when(socket.getId()).thenReturn(socketId);


        when(socketRepository.findById(socketId)).thenReturn(Optional.of(socket));



        Socket result = socketService.getSocketInfo(socketId);


        assertNotNull(result);
        assertEquals(socketId, result.getId());
        verify(socketRepository, times(1)).findById(socketId);
    }

    @Test
    public void 개별_콘센트_조회_실패_소켓없음() {
        Long socketId = 1L;

        when(socketRepository.findById(socketId)).thenReturn(Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            socketService.getSocketInfo(socketId);
        });

        assertEquals(ErrorStatus.SOCKET_NOT_FOUND, thrown.getCode());
        verify(socketRepository, times(1)).findById(socketId);
    }

    @Test
    void 층소켓조회_성공() {

        Building mockBuilding = Mockito.mock(Building.class);
        when(mockBuilding.getName()).thenReturn("s1_building1");

        Floor mockFloor = Mockito.mock(Floor.class);
        when(mockFloor.getBuilding()).thenReturn(mockBuilding);

        Space mockSpace = Mockito.mock(Space.class);
        when(mockSpace.getFloor()).thenReturn(mockFloor);
        when(mockSpace.getName()).thenReturn("s1_floor1");

        Socket mockSocket = Mockito.mock(Socket.class);
        when(mockSocket.getId()).thenReturn(1L);
        when(mockSocket.getName()).thenReturn("s1");
        when(mockSocket.getImage()).thenReturn("s1_image");
        when(mockSocket.getSpace()).thenReturn(mockSpace);

        List<Space> mockSpaceList = new ArrayList<>();
        mockSpaceList.add(mockSpace);

        List<Socket> mockSocketList = new ArrayList<>();
        mockSocketList.add(mockSocket);


        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mockBuilding));
        when(floorRepository.findByBuildingAndLevel(mockBuilding, 2)).thenReturn(mockFloor);
        when(spaceRepository.findAllByFloor(mockFloor)).thenReturn(mockSpaceList);
        when(socketRepository.findAllBySpace(mockSpace)).thenReturn(mockSocketList);


        List<Socket> result = socketService.getSocketList(1L, 2);


        assertEquals(1, result.size());
        assertEquals(mockSocket, result.get(0));
    }
}
