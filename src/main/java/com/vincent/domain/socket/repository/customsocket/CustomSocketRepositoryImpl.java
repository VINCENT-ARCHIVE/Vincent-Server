package com.vincent.domain.socket.repository.customsocket;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.repository.customspace.CustomSpaceRepository;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class CustomSocketRepositoryImpl implements CustomSocketRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SocketPlace findSocketPlaceBySocketId(Long socketId) {

        String jpql = "SELECT new com.vincent.domain.socket.controller.dto.SocketResponseDto$SocketPlace("
            + "building.id, "
            + "floor.level ) "
            + "FROM Socket socket "
            + "JOIN socket.space space "
            + "JOIN space.floor floor "
            + "JOIN floor.building building "
            + "WHERE socket.id = :socketId";

        return entityManager.createQuery(jpql, SocketPlace.class)
            .setParameter("socketId", socketId)
            .getSingleResult();
    }


    @Override
    public List<Socket> findSocketListByBuildingIdAndLevel(Long buildingId, Integer level) {

        String jpql = "SELECT socket "
            + "FROM Socket socket "
            + "JOIN socket.space space "
            + "JOIN space.floor floor "
            + "JOIN floor.building building "
            + "WHERE building.id = :buildingId AND floor.level = :level";

        return entityManager.createQuery(jpql, Socket.class)
            .setParameter("buildingId", buildingId)
            .setParameter("level", level)
            .getResultList();
    }
}
