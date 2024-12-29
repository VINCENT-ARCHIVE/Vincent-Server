package com.vincent.domain.socket;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
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

public class TestSocketRepository implements SocketRepository {

    List<Socket> sockets = new ArrayList<>();
    List<Space> spaces = new ArrayList<>();
    List<Floor> floors = new ArrayList<>();
    List<Building> buildings = new ArrayList<>();



    @Override
    public List<Socket> findAllBySpace(Space space) {
        return sockets.stream()
            .filter(socket -> socket.getSpace().equals(space))
            .collect(Collectors.toList());
    }

    @Override
    public SocketPlace findSocketPlaceBySocketId(Long socketId) {
        Socket socket = sockets.stream()
            .filter(s -> s.getId().equals(socketId))
            .findFirst()
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND)); // Socket이 없을 경우 예외

        Space space = Optional.ofNullable(socket.getSpace())
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SPACE_NOT_FOUND)); // Space가 없을 경우 예외

        Floor floor = Optional.ofNullable(space.getFloor())
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.FLOOR_NOT_FOUND)); // Floor가 없을 경우 예외

        Building building = Optional.ofNullable(floor.getBuilding())
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.BUILDING_NOT_FOUND)); // Building이 없을 경우 예외

        // 모든 데이터가 유효한 경우 DTO 생성
        return SocketPlace.builder()
            .buildingId(building.getId())
            .level(floor.getLevel())
            .build();
    }





    @Override
    public List<Socket> findSocketListByBuildingIdAndLevel(Long buildingId, Integer level) {
        return buildings.stream()
            .filter(building -> building.getId().equals(buildingId))
            .flatMap(building -> floors.stream()
                .filter(floor -> floor.getBuilding().equals(building) && floor.getLevel().equals(level))
                .flatMap(floor -> spaces.stream()
                    .filter(space -> space.getFloor().equals(floor))
                    .flatMap(space -> sockets.stream()
                        .filter(socket -> socket.getSpace().equals(space))
                    )
                )
            )
            .collect(Collectors.toList());
    }

    @Override
    public Socket save(Socket socket) {
        // 중복 데이터 제거
        sockets.removeIf(existingSocket -> existingSocket.getId().equals(socket.getId()));
        sockets.add(socket);

        // 관련 데이터 추가
        if (socket.getSpace() != null) {
            spaces.removeIf(existingSpace -> existingSpace.getId().equals(socket.getSpace().getId()));
            spaces.add(socket.getSpace());
            if (socket.getSpace().getFloor() != null) {
                floors.removeIf(existingFloor -> existingFloor.getId().equals(socket.getSpace().getFloor().getId()));
                floors.add(socket.getSpace().getFloor());
                if (socket.getSpace().getFloor().getBuilding() != null) {
                    buildings.removeIf(existingBuilding -> existingBuilding.getId().equals(socket.getSpace().getFloor().getBuilding().getId()));
                    buildings.add(socket.getSpace().getFloor().getBuilding());
                }
            }
        }

        return socket;
    }





    @Override
    public List<Socket> findAll() {
        return sockets;
    }

    @Override
    public <S extends Socket> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Socket> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Socket> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Socket> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Socket getOne(Long aLong) {
        return null;
    }

    @Override
    public Socket getById(Long aLong) {
        return null;
    }

    @Override
    public Socket getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Socket> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Socket> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Socket> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Socket> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Socket> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Socket, R> R findBy(Example<S> example,
        Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Socket> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Socket> findById(Long id) {
        return sockets.stream()
            .filter(socket -> socket.getId().equals(id))
            .findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Socket> findAllById(Iterable<Long> longs) {
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
    public void delete(Socket entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Socket> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Socket> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Socket> findAll(Pageable pageable) {
        return null;
    }
}
