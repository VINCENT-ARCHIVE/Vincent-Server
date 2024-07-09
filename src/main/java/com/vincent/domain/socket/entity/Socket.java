package com.vincent.domain.socket.entity;

import com.vincent.domain.building.entity.Building;
import jakarta.persistence.*;

public class Socket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="socket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    private int holes;

    private int x_coordinate;

    private int y_coordinate;

    @Column(columnDefinition = "TEXT")
    private String image;


}
