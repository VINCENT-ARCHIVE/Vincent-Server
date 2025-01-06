package com.vincent.domain.socket.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.TestSocketRepository;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SocketServiceTest {

    private SocketRepository socketRepository;
    private SocketDataService socketDataService;
    private SocketService socketService;

    @BeforeEach
    void setUp(){
        socketRepository = new TestSocketRepository();
        socketDataService = new SocketDataService(socketRepository);
        socketService = new SocketService(socketDataService);
    }


    @Test
    public void 개별_콘센트_조회_성공() {
        //given
        Long socketId = 1L;
        Socket socket = Socket.builder().id(1L).build();
        socketDataService.save(socket);

        //when
        Socket result = socketService.getSocketInfo(socketId);

        //then
        assertEquals(socket, result);
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

        socketDataService.save(socket1);
        socketDataService.save(socket2);

        //when
        List<Socket> result = socketService.getSocketList(buildingId, level);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(expectedSockets, result);
    }

    @Test
    void 소켓장소조회_성공() {

        //given
        Long socketId = 1L;
        Long buildingId = 1L;
        int level = 2;
        Building building = Building.builder().id(buildingId).build();
        Floor floor = Floor.builder().id(1L).level(level).building(building).build();
        Space space = Space.builder().id(1L).floor(floor).build();
        Socket socket = Socket.builder().id(socketId).space(space).build();
        socketDataService.save(socket);

        //when
        Socket result = socketService.getSocketPlace(socketId);

        //then

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSpace().getFloor().getBuilding().getId(), buildingId);
        Assertions.assertEquals(result.getSpace().getFloor().getLevel(), level);
    }
}
