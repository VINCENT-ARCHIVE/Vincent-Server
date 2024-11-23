package com.vincent.logging;

import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.logs.entity.ApiLogs;
import com.vincent.logs.repository.ApiLogsRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Autowired
    private ApiLogsRepository apiLogsRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String uri = request.getRequestURI();
        if (isSwaggerRequest(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        MDC.put("traceId", UUID.randomUUID().toString());
        long startTime = System.currentTimeMillis();
        Long memberId = extractMemberId();
        String method = request.getMethod();               // HTTP 메서드 (GET, POST 등)
        String endpoint = request.getRequestURI();         // 요청한 URI
        String ip = getClientIp(request);       // 요청을 보낸 IP 주소

        if (memberId != null) {
            MDC.put("memberId", memberId.toString());
        } else {
            MDC.put("memberId", "anonymous");  // 또는 "unknown" 등으로 설정 가능
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            int statusCode = response.getStatus();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logRequestResponse(statusCode, memberId, method, endpoint, duration, ip);
            MDC.clear();  // MDC 정리
        }
    }

    private static Long extractMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof PrincipalDetails) {
                PrincipalDetails principalDetails = (PrincipalDetails) principal;
                return principalDetails.getMemberId();
            } else {
                return null;
            }
        }
        return null;
    }

    private void logRequestResponse(int statusCode, Long memberId, String method, String endpoint, long duration, String ip) {
        String logMessage = String.format(
            "traceId=%s, memberId=%s, method=%s, endpoint=%s, statusCode=%d, requestTime=%dms, ip=%s",
            MDC.get("traceId"), memberId, method, endpoint, statusCode, duration, ip
        );

        if (statusCode >= 200 && statusCode < 300) {
            log.info(logMessage);
            saveApiLogs("INFO", logMessage);
        } else if (statusCode >= 400 && statusCode < 500) {
            log.warn(logMessage);
            saveApiLogs("WARN", logMessage);
        } else if (statusCode >= 500) {
            log.error(logMessage);
            saveApiLogs("ERROR", logMessage);
        }
    }

    private void saveApiLogs(String level, String message) {
        String[] parts = message.split(", ");
        String traceId = extractValue(parts[0], "traceId");
        String memberId = extractValue(parts[1], "memberId");
        String method = extractValue(parts[2], "method");
        String endpoint = extractValue(parts[3], "endpoint");
        String statusCode = extractValue(parts[4], "statusCode");
        String requestTime = extractValue(parts[5], "requestTime");
        String ip = extractValue(parts[6], "ip");
        ApiLogs apiLog = ApiLogs.builder()
            .level(level)
            .traceId(traceId)
            .memberId("null".equals(memberId) ? null : memberId)
            .method(method)
            .endpoint(endpoint)
            .statusCode(statusCode)
            .requestTime(requestTime)
            .ip(ip)
            .build();
        apiLogsRepository.save(apiLog);
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress.split(",")[0].trim();  // 복수의 IP가 있을 경우 첫 번째 IP 추출
    }

    private boolean isSwaggerRequest(String uri) {
        return uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs");
    }

    private String extractValue(String part, String key) {
        return part.split("=")[1].trim();
    }
}
