package com.vincent.domain.socket.entity;

import com.vincent.domain.building.entity.Building;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Socket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="socket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    private int holes;

    private double x_coordinate;

    private double y_coordinate;

    @Column(columnDefinition = "TEXT")
    private String image;


}
