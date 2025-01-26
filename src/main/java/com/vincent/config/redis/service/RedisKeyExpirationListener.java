package com.vincent.config.redis.service;

import com.vincent.domain.iot.service.IotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private IotService iotService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        handleExpiredKey(expiredKey);
        log.info("만료된 키: {}", expiredKey);
    }

    private void handleExpiredKey(String key) {
        if (key.startsWith("iot:")) {
            Long deviceId = Long.parseLong(key.split(":")[1]);
            iotService.updateSocketStatus(deviceId, 0); // 만료된 경우 콘센트 상태 비활성화
        }
    }

}
