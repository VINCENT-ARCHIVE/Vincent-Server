package com.vincent.domain.iot.service;

import com.vincent.config.redis.service.RedisService;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Transactional
    public Iot create(Long deviceId, String socketId) {
        Socket socket = socketDataService.findByUniqueId(socketId);
        Iot iot = Iot.builder().deviceId(deviceId).socket(socket).build();
        return iotDataService.save(iot);
    }


    /**
     * IoT 데이터를 수신하고 소켓 상태 갱신
     */
    @Transactional
    public void updateSocketStatus(Long deviceId, int motionStatus) {
        // Redis에 IoT 상태 갱신
        redisService.updateDeviceStatus(deviceId, motionStatus);

        String redisKey = "iot:" + deviceId;
        Iot iot = iotDataService.findByDeviceId(deviceId);

        if (motionStatus == 1) {
            // 움직임 있음: 즉시 isUsing = true
            updateSocketIsUsing(iot, true);
        } else if (motionStatus == 0) {
            // 움직임 없음: 10분 후 상태 변경
            scheduler.schedule(() -> {
                // Redis TTL 확인 후 만료되었다면 상태 변경
                if (redisService.isTTLExpired(redisKey)) {
                    updateSocketIsUsing(iot, false);
                }
            }, 10, TimeUnit.MINUTES);
        }
    }

    /**
     * 소켓 상태를 변경
     */
    @Transactional
    public void updateSocketIsUsing(Iot iot, boolean isUsing) {
        if (!iot.getSocket().getIsUsing().equals(isUsing)) {
            iot.getSocket().setIsUsing(isUsing);
        }
    }


}
