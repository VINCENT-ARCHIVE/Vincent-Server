package com.vincent.logs;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.vincent.config.aws.s3.S3Service;
import com.vincent.logs.entity.ApiLogs;
import com.vincent.logs.entity.DailyActiveUsers;
import com.vincent.logs.repository.ApiLogsRepository;
import com.vincent.logs.repository.DailyActiveUsersRepository;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final ApiLogsRepository apiLogsRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd");


    public void calculateDailyUsers() {
        System.out.println("logConfig = ");
        LocalDateTime startOfYesterday = LocalDateTime.now(ZoneId.systemDefault()).minusDays(1)
            .with(LocalTime.MIN);
        LocalDateTime endOfYesterday = LocalDateTime.now(ZoneId.systemDefault()).minusDays(1)
            .with(LocalTime.MAX);

        List<ApiLogs> apiLogs = apiLogsRepository.findByCreatedAtBetween(
            startOfYesterday, endOfYesterday);

        Pattern pattern = Pattern.compile("memberId=(\\d+)");
        Set<Long> uniqueMemberIds = new HashSet<>();

        // 각 로그에서 memberId 추출 및 집합에 추가
        apiLogs.stream().forEach(log -> {
            String message = log.getMessage();
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                Long memberId = Long.parseLong(matcher.group(1));
                uniqueMemberIds.add(memberId);
            }
        });

        DailyActiveUsers dailyActiveUsers = DailyActiveUsers.builder()
            .date(LocalDate.now().minusDays(1))
            .activeUsers(uniqueMemberIds.size())
            .build();

        dailyActiveUsersRepository.save(dailyActiveUsers);
    }

    public void uploadLogs() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
//        String requestLogFile = logConfig.getRequestLogFile(yesterday);
        String logbackFile = logConfig.getLogbackFile(yesterday);
//        s3Service.uploadLog(requestLogFile, "logs/user-requests-" + yesterday.toString() + ".log");
        s3Service.uploadLog(logbackFile, "logs/logback-" + yesterday.toString() + ".log");
    }

    public void rollLogs() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        backupAndClearFile(logConfig.getLogbackFile(), logConfig.getLogbackFile(yesterday));

//        backupAndClearFile(logConfig.getRequestLogFile(), logConfig.getRequestLogFile(yesterday));
    }

    private void backupAndClearFile(String sourceFilePath, String backupFilePath) {
        File sourceFile = new File(sourceFilePath);

        if (sourceFile.exists()) {
            try {
                Files.copy(Paths.get(sourceFilePath), Paths.get(backupFilePath),
                    StandardCopyOption.REPLACE_EXISTING);
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
