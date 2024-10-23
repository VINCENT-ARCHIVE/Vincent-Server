package com.vincent.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.config.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class BlackListFilter extends OncePerRequestFilter {

    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtProvider.resolveToken(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (redisService.isBlacklisted(accessToken)) {
            response.setContentType("application/json");
            ApiResponse<Object> baseResponseDto = ApiResponse.onFailure(
                ErrorStatus.JWT_TOKEN_LOGOUT);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getOutputStream(), baseResponseDto);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
