package com.vincent.domain.building.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.SpaceRepository;
import com.vincent.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceDataService {

    private final SpaceRepository spaceRepository;

    public Space save(Space space) {
        return spaceRepository.save(space);
    }

    public Space findById(Long id) {
        return spaceRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SPACE_NOT_FOUND));
    }



}
