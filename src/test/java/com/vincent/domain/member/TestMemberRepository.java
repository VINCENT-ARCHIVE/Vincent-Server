package com.vincent.domain.member;

import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class TestMemberRepository implements MemberRepository {

    List<Member> members = new ArrayList<>();

    @Override
    public Optional<Member> findByEmail(String email) {
        return members.stream()
            .filter(member -> member.getEmail().equals(email))
            .findFirst();
    }

    @Override
    public Optional<Member> findByEmailAndSocialType(String email, SocialType socialType) {
        return members.stream()
            .filter(member -> member.getEmail().equals(email) && member.getSocialType()
                .equals(socialType))
            .findFirst();
    }


    @Override
    public <S extends Member> S save(S entity) {
        members.add(entity);
        return entity;
    }

    @Override
    public Optional<Member> findById(Long aLong) {
        return members.stream()
            .filter(member -> member.getId().equals(aLong))
            .findFirst();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Member> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Member> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Member> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Member getOne(Long aLong) {
        return null;
    }

    @Override
    public Member getById(Long aLong) {
        return null;
    }

    @Override
    public Member getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Member> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Member> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Member> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Member> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Member> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Member> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Member, R> R findBy(Example<S> example,
        Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Member> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Member> findAll() {
        return List.of();
    }

    @Override
    public List<Member> findAllById(Iterable<Long> longs) {
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
    public void delete(Member entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Member> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Member> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return null;
    }
}
