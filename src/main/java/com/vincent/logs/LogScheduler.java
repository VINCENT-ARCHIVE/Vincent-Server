package com.vincent.logs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogScheduler {
    private final LogService logService;

    @Scheduled(cron = "0 3 0 * * *")
    public void triggerLogRolling() {
        logService.rollLogs();
    }

    @Scheduled(cron = "0 4 0 * * *")
    public void calculateDailyActiveUsers() {
        logService.calculateDailyUsers();
    }

    @Scheduled(cron = "0 4 0 * * *")
    public void saveDailyLogs() {
        logService.uploadLogs();
    }

}
