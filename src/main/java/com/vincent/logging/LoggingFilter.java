package com.vincent.logging;

import com.vincent.config.security.principal.PrincipalDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
            filterChain.doFilter(request, response);
            return;
        }
        MDC.put("traceId", UUID.randomUUID().toString());
        long startTime = System.currentTimeMillis();
        Long memberId = extractMemberId();
        String method = request.getMethod();               // HTTP 메서드 (GET, POST 등)
        String endpoint = request.getRequestURI();         // 요청한 URI
        String ip = request.getRemoteAddr();               // 요청을 보낸 IP 주소

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

            // Principal이 PrincipalDetails 타입인지 확인
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
        } else if (statusCode >= 400 && statusCode < 500) {
            log.warn(logMessage);
        } else if (statusCode >= 500) {
            log.error(logMessage);
        }
    }
}
