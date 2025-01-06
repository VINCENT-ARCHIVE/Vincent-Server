package com.vincent.domain.iot.entity;

import com.vincent.domain.socket.entity.Socket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Iot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iot_id")
    private Long id;

    private Long deviceId;

    @OneToOne
    @JoinColumn(name = "socket_id", nullable = false)
    private Socket socket;
}
