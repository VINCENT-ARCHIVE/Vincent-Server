package com.vincent.domain.building.repository.customspace;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.repository.customfloor.CustomFloorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class CustomSpaceRepositoryImpl implements CustomSpaceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<SpaceInfoProjection> findSpaceInfoByBuildingIdAndLevel(Long buildingId, int level) {
        String jpql = "SELECT "
            + "s.name AS spaceName, "
            + "s.xCoordinate AS xCoordinate, "
            + "s.yCoordinate AS yCoordinate, "
            + "s.isSocketExist AS isSocketExist "
            + "FROM Floor f "
            + "JOIN Space s "
            + "ON f.id = s.floor.id "
            + "WHERE f.building.id = :buildingId AND f.level = :level";

        return entityManager.createQuery(jpql, SpaceInfoProjection.class)
            .setParameter("buildingId", buildingId)
            .setParameter("level", level)
            .getResultList();
    }

}
