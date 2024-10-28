package com.vincent.logs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.vincent.logs.entity.ApiLogs;
import com.vincent.logs.repository.ApiLogsRepository;

//public class DatabaseAppender extends AppenderBase<ILoggingEvent> {
//
//    private LogService logService;
//
//    @Override
//    protected void append(ILoggingEvent eventObject) {
//
//        Level level = eventObject.getLevel();
//        if (level == Level.INFO || level == Level.WARN || level == Level.ERROR) {
//            ApiLogs apiLogs = ApiLogs.builder()
//                .level(level.toString())  // 로그 레벨을 문자열로 변환하여 저장
//                .message(eventObject.getFormattedMessage())
//                .build();
//            apiLogsRepository.save(apiLogs);
//        }
//    }
//}
