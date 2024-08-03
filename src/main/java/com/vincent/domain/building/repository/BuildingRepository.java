package com.vincent.domain.building.repository;

import com.vincent.domain.building.entity.Building;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Query(value = "SELECT * FROM Building b WHERE b.name LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY CASE " +
            "WHEN b.name = :keyword THEN 0 " +
            "WHEN b.name LIKE CONCAT(:keyword, '%') THEN 1 " +
            "WHEN b.name LIKE CONCAT('%', :keyword, '%') THEN 2 " +
            "WHEN b.name LIKE CONCAT('%', :keyword) THEN 3 " +
            "ELSE 4 END",
            countQuery = "SELECT count(*) FROM Building b WHERE b.name LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<Building> findByNameContainingOrderBySimilarity(@Param("keyword") String keyword,
            PageRequest pageRequest);

}
