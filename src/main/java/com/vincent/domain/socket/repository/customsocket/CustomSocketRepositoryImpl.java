package com.vincent.domain.socket.repository.customsocket;

import com.vincent.domain.socket.entity.Socket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public class CustomSocketRepositoryImpl implements CustomSocketRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Socket> findSocketPlaceBySocketId(Long socketId) {
        String jpql = "SELECT socket FROM Socket socket "
            + "JOIN socket.space space "
            + "JOIN space.floor floor "
            + "JOIN floor.building building "
            + "WHERE socket.id = :socketId";

        List<Socket> sockets = entityManager.createQuery(jpql, Socket.class)
            .setParameter("socketId", socketId)
            .getResultList();
        return sockets.stream().findFirst();
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
