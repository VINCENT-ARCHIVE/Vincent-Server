package com.vincent.config.security.provider;

import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtProvider implements InitializingBean {

    @Value("${jwt.token.secret}")
    private String secret;
    private static SecretKey secretKey;
    private static final Long expireAccessMs = 1000L * 60 * 60; //30분

    private static final Long expireRefreshMs = 1000L * 60 * 60 * 24 * 7;  //7일

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(secret);
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("memberId", Long.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .getExpiration().before(new Date());
    }

    public Long getExpireAccessMs(String accessToken) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken)
            .getPayload()
            .getExpiration().getTime();
    }

    public String createAccessToken(Long memberId, String email) {
        return Jwts.builder().claim("memberId", memberId).claim("email", email)
            .claim("role", "user")
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireAccessMs))
            .signWith(secretKey)
            .compact();
    }

    public String createAccessToken(Long memberId, String email, SocialType socialType) {
        return Jwts.builder().claim("memberId", memberId).claim("email", email).claim("socialType", socialType)
            .claim("role", "user")
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireAccessMs))
            .signWith(secretKey)
            .compact();
    }

    public String createAccessToken(String email) {
        return Jwts.builder().claim("email", email).issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireAccessMs))
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken(Long memberId, String email) {
        return Jwts.builder().claim("memberId", memberId).claim("email", email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireRefreshMs))
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder().claim("email", email).issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireRefreshMs))
            .signWith(secretKey)
            .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .getExpiration();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredHandler("Expired Token Exception");
        } catch (UnsupportedJwtException | SecurityException | MalformedJwtException
                 | NullPointerException e) {
            throw new JwtInvalidHandler("Invalid Token Exception");
        }
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
