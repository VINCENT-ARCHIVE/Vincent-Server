package com.vincent.domain.building.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Building {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="building_id")
    private Long id;

    @Column(nullable = false, length = 25)
    private String name;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String image;

    private double x_coordinate;

    private double y_coordinate;

}
