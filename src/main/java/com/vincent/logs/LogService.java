package com.vincent.logs;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.logs.entity.DailyActiveUsers;
import com.vincent.logs.repository.DailyActiveUsersRepository;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogConfig logConfig;
    private final LogProcessor logProcessor;
    private final DailyActiveUsersRepository dailyActiveUsersRepository;
    private final S3Service s3Service;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public void calculateDailyUsers() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String logFile = logConfig.getRequestLogFile(yesterday);
        int dau = logProcessor.calculateDAU(logFile);

        DailyActiveUsers dailyActiveUsers = DailyActiveUsers.builder().activeUsers(dau)
            .date(yesterday).build();

        dailyActiveUsersRepository.save(dailyActiveUsers);
    }

    public void uploadLogs() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String requestLogFile = logConfig.getRequestLogFile(yesterday);
        String logbackFile = logConfig.getLogbackFile(yesterday);
        s3Service.uploadLog(requestLogFile, "logs/user-requests-" + yesterday.toString() + ".log");
        s3Service.uploadLog(logbackFile, "logs/logback-" + yesterday.toString() + ".log");
    }

    public void rollLogs() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        backupAndClearFile(logConfig.getLogbackFile(), logConfig.getLogbackFile(yesterday));

        backupAndClearFile(logConfig.getRequestLogFile(), logConfig.getRequestLogFile(yesterday));
    }

    private void backupAndClearFile(String sourceFilePath, String backupFilePath) {
        File sourceFile = new File(sourceFilePath);

        if (sourceFile.exists()) {
            try {
                Files.copy(Paths.get(sourceFilePath), Paths.get(backupFilePath), StandardCopyOption.REPLACE_EXISTING);
                log.info("{} 백업 완료: {}", sourceFilePath, backupFilePath);
                new PrintWriter(sourceFilePath).close();
                log.info("{} 파일 내용 비움.", sourceFilePath);
            } catch (IOException e) {
                log.error("로그 파일 백업 중 오류 발생: {}", e.getMessage());
            }
        } else {
            log.warn("파일이 존재하지 않거나 빈 파일입니다: {}", sourceFilePath);
        }
    }

}
