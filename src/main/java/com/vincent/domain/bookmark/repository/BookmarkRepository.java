package com.vincent.domain.bookmark.repository;

import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {


    Boolean existsBySocketAndMember(Socket socket, Member member);

}
