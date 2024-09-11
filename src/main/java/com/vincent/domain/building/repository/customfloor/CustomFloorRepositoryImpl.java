package com.vincent.domain.building.repository.customfloor;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class CustomFloorRepositoryImpl implements CustomFloorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public FloorInfoProjection findFloorInfoByBuildingIdAndLevel(Long buildingId, int level) {

        String jpql = "SELECT "
            + "b.name AS buildingName, "
            + "(SELECT COUNT(f2) FROM Floor f2 WHERE f2.building.id = :buildingId) AS floors, "
            + "f.level AS level, "
            + "f.image AS image "
            + "FROM Building b "
            + "JOIN Floor f "
            + "ON b.id = f.building.id "
            + "WHERE b.id = :buildingId AND f.level = :level";

        return entityManager.createQuery(jpql, FloorInfoProjection.class)
            .setParameter("buildingId", buildingId)
            .setParameter("level", level)
            .getSingleResult();
    }

}
