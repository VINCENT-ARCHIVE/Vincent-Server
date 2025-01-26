package com.vincent.domain.iot.entity;

import com.vincent.domain.iot.entity.enums.MotionStatus;
import com.vincent.domain.socket.entity.Socket;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(unique = true, nullable = false)
    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'INACTIVE'")
    private MotionStatus motionStatus;

    @OneToOne
    @JoinColumn(name = "socket_id", nullable = false)
    private Socket socket;

    public void updateMotionStatus(MotionStatus motionStatus) {
        this.motionStatus = motionStatus;
    }
}
