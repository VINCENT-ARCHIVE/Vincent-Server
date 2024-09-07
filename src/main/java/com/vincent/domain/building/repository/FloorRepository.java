package com.vincent.domain.building.repository;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.repository.customfloor.CustomFloorRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloorRepository extends JpaRepository<Floor, Long>, CustomFloorRepository {


    Floor findByBuildingAndLevel(Building building, Integer level);


    /*
    @Query("SELECT "
        + "b.name AS buildingName, "
        + "(SELECT COUNT(f2) FROM Floor f2 WHERE f2.building.id = :buildingId) AS floors, "
        + "f.level AS level, "
        + "f.image AS image "
        + "FROM Building b "
        + "JOIN Floor f "
        + "ON b.id = f.building.id "
        + "WHERE b.id = :buildingId AND f.level = :level")
        // spaceInfoList를 제외
    FloorInfoProjection findFloorInfoByBuildingIdAndLevel(
        @Param("buildingId") Long buildingId,
        @Param("level") int level
    );

     */


}
