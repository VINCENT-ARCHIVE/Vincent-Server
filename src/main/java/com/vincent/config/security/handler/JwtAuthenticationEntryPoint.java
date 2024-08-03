package com.vincent.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        log.error("JwtAuthenticationEntryPoint 실행: {}", authException.getMessage(), authException);
        response.setContentType("application/json");

        ApiResponse<Object> baseResponseDto =
                ApiResponse.onFailure(
                        ErrorStatus.JWT_TOKEN_NOT_FOUND.getCode(),
                        ErrorStatus.JWT_TOKEN_NOT_FOUND.getMessage(),
                        null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), baseResponseDto);
    }
}
