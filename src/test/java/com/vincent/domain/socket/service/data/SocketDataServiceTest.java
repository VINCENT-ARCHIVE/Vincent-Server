package com.vincent.domain.socket.service.data;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
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
class SocketDataServiceTest {
    @Mock
    private SocketRepository socketRepository;

    @InjectMocks
    private SocketDataService socketDataService;

    @Test
    void 아아디로_소켓찾기_성공() {
        //given
        Long socketId = 1L;
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(socketRepository.findById(socketId)).thenReturn(Optional.of(socket));

        //then
        Socket result = socketDataService.findById(socketId);
        Assertions.assertEquals(result,socket);
        verify(socketRepository, times(1)).findById(socketId);
    }

    @Test
    void 아아디로_소켓찾기_실패() {
        //given
        Long socketId = 1L;

        //when
        when(socketRepository.findById(socketId)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> socketDataService.findById(socketId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.SOCKET_NOT_FOUND);
    }

    @Test
    void 공간으로_소켓_찾기() {
        //given
        Space space = Space.builder().id(1L).build();
        Socket socket1 = Socket.builder().id(1L).space(space).build();
        Socket socket2 = Socket.builder().id(2L).space(space).build();
        List<Socket> sockets = Arrays.asList(socket1, socket2);

        //when
        when(socketRepository.findAllBySpace(space)).thenReturn(sockets);

        //then
        List<Socket> result = socketDataService.findAllBySpace(space);
        Assertions.assertEquals(result.size(), sockets.size());
        Assertions.assertIterableEquals(result, sockets);
        verify(socketRepository, times(1)).findAllBySpace(space);
    }


    @Test
    void 저장() {
        //given
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(socketRepository.save(socket)).thenReturn(socket);

        //then
        Socket result = socketDataService.save(socket);
        Assertions.assertNotNull(result);
        verify(socketRepository).save(socket);
    }

    @Test
    void 소켓아이디로_빌딩아이디와_층_찾기_성공() {
        //given
        Long socketId = 1L;
        Socket socket = Socket.builder().id(1L).build();
        SocketResponseDto.SocketPlace socketPlace;
        socketPlace = SocketPlace.builder().buildingId(1L).level(1).build();

        //when
        when(socketRepository.findById(socketId)).thenReturn(Optional.of(socket));
        when(socketRepository.findSocketPlaceBySocketId(socketId)).thenReturn(socketPlace);

        //then
        SocketPlace result = socketDataService.findSocketPlaceBySocketId(socketId);
        Assertions.assertEquals(result.getBuildingId(), socketPlace.getBuildingId());
        Assertions.assertEquals(result.getLevel(), socketPlace.getLevel());

        //verify
        verify(socketRepository, times(1)).findById(socketId);
        verify(socketRepository, times(1)).findSocketPlaceBySocketId(socketId);



    }

    @Test
    void 소켓아이디로_빌딩아이디와_층_찾기_실패() {

        // given
        Long invalidSocketId = 9L;

        //when
        when(socketRepository.findById(invalidSocketId)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> socketDataService.findById(invalidSocketId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.SOCKET_NOT_FOUND);

        //verify
        verify(socketRepository, times(1)).findById(invalidSocketId);
        verify(socketRepository, never()).findSocketPlaceBySocketId(invalidSocketId);



    }

}
