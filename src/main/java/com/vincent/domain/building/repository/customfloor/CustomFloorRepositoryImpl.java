package com.vincent.domain.building.repository.customfloor;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class CustomFloorRepositoryImpl implements CustomFloorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FloorInfoProjection findFloorInfoByBuildingIdAndLevel(Long buildingId, int level) {

        String jpql = "SELECT new com.vincent.domain.building.controller.dto.BuildingResponseDto$FloorInfoProjection("
            + "b.name, "
            + "(SELECT COUNT(f2.id) FROM Floor f2 WHERE f2.building.id = :buildingId), "
            + "f.level, "
            + "f.image) "
            + "FROM Building b "
            + "JOIN Floor f ON b.id = f.building.id "
            + "WHERE b.id = :buildingId AND f.level = :level";

        // JPQL Query 실행
        return entityManager.createQuery(jpql, FloorInfoProjection.class)
            .setParameter("buildingId", buildingId)
            .setParameter("level", level)
            .getSingleResult();
    }

    @Override
    public List<FloorWithSocket> findFloorWithSocketByBuildingId(Long buildingId) {

        String jpql = "SELECT new com.vincent.domain.building.controller.dto.BuildingResponseDto$FloorWithSocket("
            + "f.id, "
            + "f.level) "
            + "FROM Floor f "
            + "WHERE f.building.id = :buildingId "
            + "ORDER BY f.level ASC";

        // JPQL Query 실행
        return entityManager.createQuery(jpql, FloorWithSocket.class)
            .setParameter("buildingId", buildingId)
            .getResultList();
    }

}
