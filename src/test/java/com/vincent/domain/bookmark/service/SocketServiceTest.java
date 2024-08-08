package com.vincent.domain.bookmark.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.domain.socket.service.SocketService;
import com.vincent.exception.handler.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import org.mockito.Spy;

public class SocketServiceTest {

    @Mock
    private SocketRepository socketRepository;

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
}
