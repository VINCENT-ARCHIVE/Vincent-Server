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
        String expiredKey = message.toString(); // 만료된 키 가져오기
        handleExpiredKey(expiredKey);
        log.info("{}상태 변경", expiredKey);
    }

    private void handleExpiredKey(String key) {
        // 키에서 deviceId 추출
        Long deviceId = Long.parseLong(key.split(":")[1]);
        iotService.updateIsSocketUsing(deviceId);
    }

}
