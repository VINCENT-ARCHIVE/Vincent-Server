package com.vincent.domain.building;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorInfoProjection;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.FloorWithSocket;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.repository.FloorRepository;
import com.vincent.domain.socket.entity.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestFloorRepository implements FloorRepository {

    List<Floor> floors = new ArrayList<>();



    @Override
    public Optional<Floor> findByBuildingAndLevel(Building building, Integer level) {
        return floors.stream()
            .filter(floor -> floor.getBuilding().equals(building) && floor.getLevel().equals(level))
            .findFirst();
    }

    @Override
    public FloorInfoProjection findFloorInfoByBuildingIdAndLevel(Long buildingId, int level) {
        throw new UnsupportedOperationException("findFloorWithSocketListByBuildingId is not supported in TestFloorRepository");
    }

    @Override
    public List<FloorWithSocket> findFloorWithSocketListByBuildingId(Long buildingId) {
        throw new UnsupportedOperationException("findFloorWithSocketListByBuildingId is not supported in TestFloorRepository");


    }


    @Override
    public void flush() {

    }

    @Override
    public <S extends Floor> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Floor> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Floor> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Floor getOne(Long aLong) {
        return null;
    }

    @Override
    public Floor getById(Long aLong) {
        return null;
    }

    @Override
    public Floor getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Floor> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Floor> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Floor> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Floor> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Floor> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Floor> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Floor, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Floor> S save(S entity) {
        floors.add(entity);
        return entity;
    }

    @Override
    public <S extends Floor> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Floor> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Floor> findAll() {
        return floors;
    }

    @Override
    public List<Floor> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Floor entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Floor> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Floor> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Floor> findAll(Pageable pageable) {
        return null;
    }
}
