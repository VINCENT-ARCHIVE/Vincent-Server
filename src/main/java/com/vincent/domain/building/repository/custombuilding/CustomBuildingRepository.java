package com.vincent.domain.building.repository.custombuilding;

import com.vincent.domain.building.entity.Building;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomBuildingRepository {

    Page<Building> findByNameContainingOrderBySimilarity(
        String keyword,
        PageRequest pageRequest);

    List<Building> findAllByLocation(
        Double longitudeLower,
        Double longitudeUpper,
        Double latitudeLower,
        Double latitudeUpper
    );

}
