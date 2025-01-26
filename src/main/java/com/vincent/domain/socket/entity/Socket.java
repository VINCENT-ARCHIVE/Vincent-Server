package com.vincent.domain.socket.entity;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Space;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import lombok.Setter;



@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Socket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "socket_id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "building_id", nullable = false)
//    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    private int holes;

    private double xCoordinate;

    private double yCoordinate;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "isUsing")
    @ColumnDefault("false")
    @Setter
    private Boolean isUsing;


    public void switchUsageStatus(){
        this.isUsing = !this.isUsing;
    }
}
