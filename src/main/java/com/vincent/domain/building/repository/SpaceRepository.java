package com.vincent.domain.building.repository;

import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpaceRepository extends JpaRepository<Space, Long> {



    List<Space> findAllByFloor(Floor floor);

    public interface SpaceInfoProjection {
        String getSpaceName();

        Double getxCoordinate();

        Double getyCoordinate();

        Boolean getIsSocketExist();
    }

    @Query("SELECT "
        + "s.name AS spaceName, "
        + "s.xCoordinate AS xCoordinate, "
        + "s.yCoordinate AS yCoordinate, "
        + "s.isSocketExist AS isSocketExist "
        + "FROM Floor f "
        + "JOIN Space s "
        + "ON f.id = s.floor.id "
        + "WHERE f.building.id = :buildingId AND f.level = :level")
    List<SpaceInfoProjection> findSpaceInfoByBuildingIdAndLevel(
        @Param("buildingId") Long buildingId,
        @Param("level") int level);


}
