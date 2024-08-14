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
        MDC.put("traceId", UUID.randomUUID().toString());
        Long memberId = extractMemberId();
        if (memberId != null) {
            MDC.put("memberId", memberId.toString());
        } else {
            MDC.put("memberId", "anonymous");  // 또는 "unknown" 등으로 설정 가능
        }
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response),
                filterChain);
        }
        MDC.clear();
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            logRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(RequestWrapper request) {
        RequestLogInfo httpLogInfo = new RequestLogInfo(request);
        log.info(httpLogInfo.toJson());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        ResponseLogInfo httpLogInfo = new ResponseLogInfo(response);
        log.info(httpLogInfo.toJson());
    }

    private static Long extractMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            // Principal이 PrincipalDetails 타입인지 확인
            if (principal instanceof PrincipalDetails) {
                PrincipalDetails principalDetails = (PrincipalDetails) principal;
                return principalDetails.getMemberId();
            } else if (principal instanceof String && principal.equals("anonymousUser")) {
                // 인증되지 않은 사용자
                System.out.println("인증되지 않은 사용자입니다.");
                return null;  // 인증되지 않은 경우 처리 (예: null 또는 기본값 반환)
            } else {
                // 다른 예상하지 못한 타입 처리
                System.out.println("예상하지 못한 Principal 타입: " + principal.getClass().getName());
                return null;
            }
        }
        return null;
    }
}
