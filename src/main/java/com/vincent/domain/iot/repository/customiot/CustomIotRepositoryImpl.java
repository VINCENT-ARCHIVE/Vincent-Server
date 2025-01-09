package com.vincent.domain.iot.repository.customiot;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.repository.customspace.CustomSpaceRepository;
import com.vincent.domain.iot.entity.Iot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class CustomIotRepositoryImpl implements CustomIotRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iot findByDeviceId(Long deviceId) {
        String jpql = "SELECT a FROM Iot a WHERE a.deviceId = :deviceId";

        return entityManager.createQuery(jpql, Iot.class)
            .setParameter("deviceId", deviceId)
            .getSingleResult();
    }
}

/*
public class CustomSpaceRepositoryImpl implements CustomSpaceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SpaceInfoProjection> findSpaceInfoByBuildingIdAndLevel(Long buildingId, int level) {
        String jpql = "SELECT new com.vincent.domain.building.controller.dto.BuildingResponseDto$SpaceInfoProjection("
            + "s.name, "
            + "s.xCoordinate, "
            + "s.yCoordinate, "
            + "s.isSocketExist) "
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

 */
