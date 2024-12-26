package com.vincent.domain.building;

import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.repository.BuildingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestBuildingRepository implements BuildingRepository {

    List<Building> buildings = new ArrayList<>();
    @Override
    public Page<Building> findByNameContainingOrderBySimilarity(String keyword, PageRequest pageRequest) {
        return null;
    }

    @Override
    public List<Building> findAllByLocation(Double longitudeLower, Double longitudeUpper, Double latitudeLower,
        Double latitudeUpper) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Building> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Building> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Building> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Building getOne(Long aLong) {
        return null;
    }

    @Override
    public Building getById(Long aLong) {
        return null;
    }

    @Override
    public Building getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Building> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Building> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Building> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Building> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Building> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Building> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Building, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Building> S save(S entity) {
        buildings.add(entity);
        return entity;
    }

    @Override
    public <S extends Building> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Building> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Building> findAll() {
        return buildings;
    }

    @Override
    public List<Building> findAllById(Iterable<Long> longs) {
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
    public void delete(Building entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Building> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Building> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Building> findAll(Pageable pageable) {
        return null;
    }
}
