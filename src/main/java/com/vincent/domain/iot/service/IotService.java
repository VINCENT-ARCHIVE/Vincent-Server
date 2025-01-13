package com.vincent.domain.iot.service;

import com.vincent.config.redis.service.RedisService;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IotService {
    private final IotDataService iotDataService;
    private final SocketDataService socketDataService;
    private final RedisService redisService;


    @Transactional
    public Iot create(Long deviceId, String socketId) {
        Socket socket = socketDataService.findByUniqueId(socketId);
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();
        return iotDataService.save(iot);
    }

    /**
     * Redis에서 IoT 데이터 읽기 및 소켓 상태 업데이트
     */
    @Transactional
    public boolean updateIsSocketUsing(Long deviceId) {
        String redisKey = "iot:" + deviceId;

        // Redis에서 데이터 읽기
        List<Object> rawStates = redisService.getList(redisKey, 0, -1);
        if (rawStates == null || rawStates.size() < 60) {
            return false; // 10분 데이터가 누적되지 않음
        }

        // String 데이터를 Integer로 변환
        List<Integer> states = rawStates.stream()
            .map(state -> Integer.valueOf((String) state))
            .collect(Collectors.toList());

        // 0과 1 개수 계산
        long countZero = states.stream().filter(s -> s == 0).count();
        long countOne = states.size() - countZero;

        // 업데이트할 상태 결정
        boolean isUsing = countOne > countZero;

        // socket 테이블 업데이트
        Iot targetIot = iotDataService.findByDeviceId(deviceId);
        Socket targetSocket = socketDataService.findById(targetIot.getSocket().getId());
        targetSocket.setIsUsing(isUsing);

        // ** 변경된 Socket을 저장 **
        socketDataService.save(targetSocket);

        // Redis 데이터 초기화
        redisService.delete(redisKey);

        return true; // 업데이트 성공
    }




}
