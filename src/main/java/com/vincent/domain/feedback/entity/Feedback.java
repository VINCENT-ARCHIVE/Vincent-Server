package com.vincent.domain.feedback.entity;

import com.vincent.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 300)
    private String content;

    private LocalDateTime createdAt;
}
