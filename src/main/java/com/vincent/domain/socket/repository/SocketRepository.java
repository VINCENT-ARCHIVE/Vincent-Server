package com.vincent.domain.socket.repository;

import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.socket.entity.Socket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocketRepository extends JpaRepository<Socket, Long> {

}
