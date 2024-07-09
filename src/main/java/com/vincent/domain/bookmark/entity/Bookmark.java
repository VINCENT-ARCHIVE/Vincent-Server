package com.vincent.domain.bookmark.entity;

import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socket_id", nullable = false)
    private Socket socket;

    private LocalDateTime createdAt;
}
