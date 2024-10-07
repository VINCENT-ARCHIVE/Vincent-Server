package com.vincent.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LogProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public int calculateDAU(String logFilePath) {
        Set<String> uniqueMemberIds = new HashSet<>(); // 고유한 memberId를 저장

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 로그 라인에서 memberId 추출
                String memberId = extractKeyFromLog(line, "memberId");
                if (!memberId.equals("null") && !memberId.equals("anonymous")) {
                    uniqueMemberIds.add(memberId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uniqueMemberIds.size();
    }

    private String extractKeyFromLog(String logLine, String key) {
        // 로그 라인에서 원하는 값을 추출하기 위한 간단한 파싱 로직
        if (logLine.contains(key + "=")) {
            int startIndex = logLine.indexOf(key + "=") + (key + "=").length();
            int endIndex = logLine.indexOf(",", startIndex);
            if (endIndex == -1) { // 마지막 값일 경우
                endIndex = logLine.length();
            }
            return logLine.substring(startIndex, endIndex).trim();
        }
        return null;
    }

}
