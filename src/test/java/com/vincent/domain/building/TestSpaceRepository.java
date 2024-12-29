package com.vincent.domain.building;

import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.building.repository.SpaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestSpaceRepository implements SpaceRepository {


    List<Space> spaces = new ArrayList<>();
    @Override
    public List<Space> findAllByFloor(Floor floor) {
        return null;
    }

    @Override
    public List<SpaceInfoProjection> findSpaceInfoByBuildingIdAndLevel(Long buildingId, int level) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Space> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Space> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Space> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Space getOne(Long aLong) {
        return null;
    }

    @Override
    public Space getById(Long aLong) {
        return null;
    }

    @Override
    public Space getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Space> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Space> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Space> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Space> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Space> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Space> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Space, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Space> S save(S entity) {
        spaces.add(entity);
        return entity;
    }

    @Override
    public <S extends Space> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Space> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Space> findAll() {
        return spaces;
    }

    @Override
    public List<Space> findAllById(Iterable<Long> longs) {
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
    public void delete(Space entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Space> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Space> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Space> findAll(Pageable pageable) {
        return null;
    }
}
