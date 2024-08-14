package com.vincent.domain.building.repository;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface FloorRepository extends JpaRepository<Floor, Long> {


    Floor findByBuildingAndLevel(Building building, Integer level);

    List<Floor> findAllByBuilding(Building building);

}
