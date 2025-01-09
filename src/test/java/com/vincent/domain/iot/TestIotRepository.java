package com.vincent.domain.iot;

import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.repository.IotRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestIotRepository implements IotRepository {

    List<Iot> iots = new ArrayList<>();

    @Override
    public Iot findByDeviceId(Long deviceId) {
        // deviceId에 해당하는 Iot를 찾음
        return iots.stream()
            .filter(iot -> iot.getDeviceId().equals(deviceId)) // deviceId가 같은 경우 필터링
            .findFirst() // 첫 번째 결과 반환
            .orElse(null); // 없으면 null 반환
    }
    @Override
    public <S extends Iot> S save(S entity) {
        iots.add(entity);
        return entity;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Iot> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Iot> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Iot> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Iot getOne(Long aLong) {
        return null;
    }

    @Override
    public Iot getById(Long aLong) {
        return null;
    }

    @Override
    public Iot getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Iot> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Iot> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Iot> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Iot> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Iot> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Iot> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Iot, R> R findBy(Example<S> example,
        Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Iot> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Iot> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Iot> findAll() {
        return List.of();
    }

    @Override
    public List<Iot> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Iot entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Iot> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Iot> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Iot> findAll(Pageable pageable) {
        return null;
    }

}
