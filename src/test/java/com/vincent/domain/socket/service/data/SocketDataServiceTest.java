package com.vincent.domain.socket.service.data;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.TestBuildingRepository;
import com.vincent.domain.building.TestFloorRepository;
import com.vincent.domain.building.TestSpaceRepository;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.BuildingRepository;
import com.vincent.domain.building.service.data.BuildingDataService;
import com.vincent.domain.socket.TestSocketRepository;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

class SocketDataServiceTest {



    private SocketRepository socketRepository;
    private SocketDataService socketDataService;


    @BeforeEach
    void setUp() {
        socketRepository = new TestSocketRepository();
        socketDataService = new SocketDataService(socketRepository);
    }

    @Test
    void 저장() {
        // given
        Socket socket = Socket.builder().id(1L).build();

        // when
        Socket result = socketDataService.save(socket);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, socket);
        Assertions.assertEquals(1, socketRepository.findAll().size()); // 저장된 데이터 확인
    }

    @Test
    void 아이디로_소켓찾기_성공() {
        // given
        Long socketId = 1L;
        Socket socket = Socket.builder().id(socketId).build();
        socketRepository.save(socket);

        // when
        Socket result = socketDataService.findById(socketId);

        // then
        Assertions.assertEquals(result, socket);
    }

    @Test
    void 아이디로_소켓찾기_실패() {
        // given
        Long invalidSocketId = 9L;

        // when & then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> socketDataService.findById(invalidSocketId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.SOCKET_NOT_FOUND);
    }

    @Test
    void 공간으로_소켓_찾기() {
        // given
        Space space = Space.builder().id(1L).build();
        Socket socket1 = Socket.builder().id(1L).space(space).build();
        Socket socket2 = Socket.builder().id(2L).space(space).build();
        socketRepository.save(socket1);
        socketRepository.save(socket2);

        // when
        List<Socket> result = socketDataService.findAllBySpace(space);

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(socket1));
        Assertions.assertTrue(result.contains(socket2));
    }

    @Test
    void 소켓아이디로_빌딩아이디와_층_찾기_성공() {
        // given
        Long socketId = 1L;
        Building building = Building.builder().id(1L).build();
        Floor floor = Floor.builder().id(1L).level(1).building(building).build();
        Space space = Space.builder().id(1L).floor(floor).build();
        Socket socket = Socket.builder().id(socketId).space(space).build();
        socketRepository.save(socket);

        // when
        Socket result = socketDataService.findSocketPlaceBySocketId(socketId);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(building.getId(), result.getSpace().getFloor().getBuilding().getId());
        Assertions.assertEquals(floor.getLevel(), result.getSpace().getFloor().getLevel());
    }

    @Test
    void 소켓아이디로_빌딩아이디와_층_찾기_실패_소켓없음() {
        // given
        Long invalidSocketId = 9L;

        // when & then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> socketDataService.findById(invalidSocketId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.SOCKET_NOT_FOUND);
    }

//    @Test
//    void 소켓아이디로_빌딩아이디와_층_찾기_실패_공간없음() {
//        // given
//        Long socketId = 1L;
//        Socket socket = Socket.builder().id(socketId).space(null).build(); // Space가 없음
//        socketRepository.save(socket);
//
//        // when & then
//        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
//            () -> socketDataService.findSocketPlaceBySocketId(socketId));
//
//        Assertions.assertEquals(ErrorStatus.SPACE_NOT_FOUND, thrown.getCode());
//    }
//
//    @Test
//    void 소켓아이디로_빌딩아이디와_층_찾기_실패_층없음() {
//        // given
//        Long socketId = 1L;
//        Space space = Space.builder().id(1L).floor(null).build(); // Floor가 없음
//        Socket socket = Socket.builder().id(socketId).space(space).build();
//        socketRepository.save(socket);
//
//        // when & then
//        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
//            () -> socketDataService.findSocketPlaceBySocketId(socketId));
//
//        Assertions.assertEquals(ErrorStatus.FLOOR_NOT_FOUND, thrown.getCode());
//    }

    @Test
    void 빌딩아이디와_층으로_소켓목록_찾기_성공() {
        // given
        Long buildingId = 1L;
        Integer level = 2;

        // 관계 설정
        Building building = Building.builder().id(buildingId).build();
        Floor floor = Floor.builder().id(1L).level(level).building(building).build();
        Space space1 = Space.builder().id(1L).floor(floor).build();
        Space space2 = Space.builder().id(2L).floor(floor).build();
        Socket socket1 = Socket.builder().id(1L).space(space1).build();
        Socket socket2 = Socket.builder().id(2L).space(space2).build();

        // 관련 데이터 명시적으로 저장
        socketRepository.save(socket1);        // Socket1 저장
        socketRepository.save(socket2);        // Socket2 저장

        // when
        List<Socket> result = socketDataService.findSocketListByBuildingIdAndLevel(buildingId, level);

        // then
        Assertions.assertNotNull(result); // 결과가 null이 아닌지 확인
        Assertions.assertEquals(2, result.size()); // 기대하는 소켓 개수 확인
        Assertions.assertTrue(result.contains(socket1)); // socket1 포함 여부 확인
        Assertions.assertTrue(result.contains(socket2)); // socket2 포함 여부 확인
    }

    @Test
    void 소켓_식별번호_조회_성공() {
        //given
        String uniqueId = "name";
        Socket socket = Socket.builder().id(1L).name("name").build();
        socketRepository.save(socket);

        //when
        Socket result = socketDataService.findByUniqueId(uniqueId);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), uniqueId);
    }

    @Test
    void 소켓_식별번호_조회_실패() {
        //given
        String uniqueId = "name";

        //when & then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> socketDataService.findByUniqueId(uniqueId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.SOCKET_NOT_FOUND);
    }

}
