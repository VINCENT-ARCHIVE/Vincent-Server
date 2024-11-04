package com.vincent.logs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ApiLogs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_logs_id")
    private Long id;

    private String level;

    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
