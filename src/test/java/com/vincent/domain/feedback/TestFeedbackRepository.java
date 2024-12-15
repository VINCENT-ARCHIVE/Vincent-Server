package com.vincent.domain.feedback;

import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.repository.FeedbackRepository;
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

public class TestFeedbackRepository implements FeedbackRepository {
    List<Feedback> feedbacks = new ArrayList<>();

    @Override
    public <S extends Feedback> S save(S entity) {
        feedbacks.add(entity);
        return entity;
    }

    @Override
    public List<Feedback> findAll() {
        return feedbacks;
    }

    @Override
    public <S extends Feedback> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Feedback> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Feedback> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Feedback> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Feedback getOne(Long aLong) {
        return null;
    }

    @Override
    public Feedback getById(Long aLong) {
        return null;
    }

    @Override
    public Feedback getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Feedback> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Feedback> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Feedback> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Feedback> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Feedback> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Feedback, R> R findBy(Example<S> example,
        Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Feedback> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Feedback> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Feedback> findAllById(Iterable<Long> longs) {
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
    public void delete(Feedback entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Feedback> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Feedback> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Feedback> findAll(Pageable pageable) {
        return null;
    }
}
