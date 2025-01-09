package com.vincent.domain.iot.service;

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
    private final RedisTemplate<String, Object> redisTemplate;


    @Transactional
    public Iot create(Long deviceId, String socketId) {
        Socket socket = socketDataService.findByUniqueId(socketId);
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();
        return iotDataService.save(iot);
    }

    public void saveIotData(Long deviceId, int isUsing) {
        String redisKey = "iot:" + deviceId;

        // Redis에 state 값을 String으로 저장
        redisTemplate.opsForList().rightPush(redisKey, String.valueOf(isUsing));
        redisTemplate.expire(redisKey, Duration.ofMinutes(10)); // TTL 설정
    }

    @Transactional
    public boolean updateIsSocketUsing(Long deviceId) {
        String redisKey = "iot:" + deviceId;

        // Redis에서 데이터 읽기
        List<Object> rawStates = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (rawStates == null || rawStates.size() < 60) {
            return false; // 10분 데이터가 누적되지 않음
        }
        // String 데이터를 Integer로 변환
        List<Integer> states = rawStates.stream()
            .map(state -> Integer.valueOf((String) state)) // String을 Integer로 변환
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
        socketDataService.save(targetSocket);


        // Redis 데이터 초기화 (처리 완료 후 삭제)
        redisTemplate.delete(redisKey);

        return true; // 업데이트 성공
    }


}
