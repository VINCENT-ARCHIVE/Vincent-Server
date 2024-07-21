package com.vincent.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String accessToken = jwtProvider.resolveToken(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwtProvider.validateToken(accessToken);
            Long memberId = jwtProvider.getMemberId(accessToken);
            String email = jwtProvider.getEmail(accessToken);

            Member member = Member.builder()
                .id(memberId)
                .email(email)
                .build();

            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtExpiredHandler e) {
            response.setContentType("application/json");
            ApiResponse<Object> baseResponseDto = ApiResponse.onFailure(
                ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED.getCode(),
                ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED.getMessage(),
                null
            );
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getOutputStream(), baseResponseDto);
            return;
        } catch (JwtInvalidHandler e) {
            response.setContentType("application/json");
            ApiResponse<Object> baseResponseDto = ApiResponse.onFailure(
                ErrorStatus.JWT_UNSUPPORTED_TOKEN.getCode(),
                ErrorStatus.JWT_UNSUPPORTED_TOKEN.getMessage(),
                null
            );
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getOutputStream(), baseResponseDto);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
