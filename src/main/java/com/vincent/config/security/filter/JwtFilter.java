package com.vincent.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
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
    private final static String[] URI_WHITE_LIST = {
        "/v1/login",
        "/v1/reissue"
    };
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        String accessToken = jwtProvider.resolveToken(request);
        if (bypassTokenValidation(accessToken, request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            processTokenValidation(accessToken, request);
        } catch (JwtExpiredHandler e) {
            writeErrorResponse(response, ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED);
            return;
        } catch (JwtInvalidHandler e) {
            writeErrorResponse(response, ErrorStatus.JWT_UNSUPPORTED_TOKEN);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean bypassTokenValidation(String accessToken, HttpServletRequest request) {
        boolean isWhiteListed = Arrays.stream(URI_WHITE_LIST)
            .anyMatch(uri -> uri.equals(request.getRequestURI()));
        return accessToken == null || isWhiteListed;
    }

    private void processTokenValidation(String accessToken, HttpServletRequest request) {
        jwtProvider.validateToken(accessToken);
        Long memberId = jwtProvider.getMemberId(accessToken);
        String email = jwtProvider.getEmail(accessToken);
        SocialType socialType = SocialType.fromString(jwtProvider.getSocialType(accessToken));

        Member member = Member.builder()
            .id(memberId)
            .email(email)
            .socialType(socialType)
            .build();

        setAuthentication(member);
    }

    private void setAuthentication(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
            );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorStatus errorStatus)
        throws IOException {
        response.setContentType("application/json");
        ApiResponse<Object> apiResponse =
            ApiResponse.onFailure(errorStatus);
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
