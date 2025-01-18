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
    public boolean setSocketUsage(Long deviceId, int isUsing) {
        String redisKey = "iot:" + deviceId;

        if(redisService.hasKey(redisKey)) {
            redisService.delete(redisKey);
        }

        boolean isDeviceUsing = isUsing == 1;

        Iot iot = iotDataService.findByDeviceId(deviceId);

        if(iot.getSocket().getIsUsing().equals(isDeviceUsing)) {
            return true;
        }

        redisService.addDeviceId(redisKey, isUsing);
        redisService.setExpire(redisKey, Duration.ofMinutes(10));
        return true;
    }

    @Transactional
    public void updateIsSocketUsing(Long deviceId) {
        Iot iot = iotDataService.findByDeviceId(deviceId);
        iot.getSocket().switchUsageStatus();
    }


}
