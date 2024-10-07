package com.vincent.logs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfig {

    private static String LOG_PATH = "logs";
    private static final String REQUEST_LOG_PATTERN = "user-requests.%s.log";
    private static final String LOGBACK_PATTERN = "logback.%s.log";
    private static final String REQUEST_LOG_FILE = "user-requests.log";
    private static final String LOGBACK_FILE = "logback.log";

    public String getRequestLogFile() {
        return LOG_PATH + "/" + REQUEST_LOG_FILE;
    }
    public String getRequestLogFile(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LOG_PATH + "/" + String.format(REQUEST_LOG_PATTERN, formattedDate);
    }

    public String getLogbackFile() {
        return LOG_PATH + "/" + LOGBACK_FILE;
    }

    public String getLogbackFile(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LOG_PATH + "/" + String.format(LOGBACK_PATTERN, formattedDate);
    }
}
