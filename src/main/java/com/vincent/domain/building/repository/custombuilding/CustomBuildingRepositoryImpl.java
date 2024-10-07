package com.vincent.domain.building.repository.custombuilding;

import com.vincent.domain.building.entity.Building;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

public class CustomBuildingRepositoryImpl implements CustomBuildingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Building> findByNameContainingOrderBySimilarity(String keyword, PageRequest pageRequest) {

        String jpql = "SELECT b FROM Building b WHERE b.name LIKE CONCAT('%', :keyword, '%') "
            + "ORDER BY CASE "
            + "WHEN b.name = :keyword THEN 0 "
            + "WHEN b.name LIKE CONCAT(:keyword, '%') THEN 1 "
            + "WHEN b.name LIKE CONCAT('%', :keyword, '%') THEN 2 "
            + "WHEN b.name LIKE CONCAT('%', :keyword) THEN 3 "
            + "ELSE 4 END";

        List<Building> buildings = entityManager.createQuery(jpql, Building.class)
            .setParameter("keyword", keyword)
            .setFirstResult((int) pageRequest.getOffset())
            .setMaxResults(pageRequest.getPageSize())
            .getResultList();

        String countJpql = "SELECT count(b) FROM Building b WHERE b.name LIKE CONCAT('%', :keyword, '%')";
        long total = (Long) entityManager.createQuery(countJpql)
            .setParameter("keyword", keyword)
            .getSingleResult();

        return new PageImpl<>(buildings, pageRequest, total);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Building> findAllByLocation(
        Double longitudeLower, Double longitudeUpper, Double latitudeLower, Double latitudeUpper) {
        String jpql = "SELECT b FROM Building b "
            + "WHERE b.longitude BETWEEN :longitudeLower AND :longitudeUpper "
            + "AND b.latitude BETWEEN :latitudeLower AND :latitudeUpper";

        return entityManager.createQuery(jpql, Building.class)
            .setParameter("longitudeLower", longitudeLower)
            .setParameter("longitudeUpper", longitudeUpper)
            .setParameter("latitudeLower", latitudeLower)
            .setParameter("latitudeUpper", latitudeUpper)
            .getResultList();
    }

}
