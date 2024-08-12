package com.vincent.domain.socket.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocketService {

    private final SocketRepository socketRepository;

    public Socket getSocketInfo(Long socketId) {

        return  socketRepository.findById(socketId).orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));
    }

}
