package com.vincent.domain.socket.repository;

import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.customsocket.CustomSocketRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocketRepository extends JpaRepository<Socket, Long>, CustomSocketRepository {

    List<Socket> findAllBySpace(Space space);

}
