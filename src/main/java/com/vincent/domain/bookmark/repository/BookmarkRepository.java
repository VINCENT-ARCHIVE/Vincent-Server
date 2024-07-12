package com.vincent.domain.bookmark.repository;

import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  @Query("SELECT b FROM Bookmark b WHERE b.socket.id = :socketId AND b.member.id = :memberId")
  Optional<Bookmark> findBySocketIdAndMemberId(@Param("socketId") Long socketId,
      @Param("memberId") Long memberId);

  Boolean existsBySocketAndMember(Socket socket, Member member);

}
