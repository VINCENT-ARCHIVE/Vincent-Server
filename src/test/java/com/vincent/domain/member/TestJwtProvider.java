package com.vincent.domain.member;

import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

public class TestJwtProvider extends JwtProvider {

    private static final String TEST_SECRET = "bG1HZ2VuRElBT2hUS2pzSkFDdnY5elNFU0JKQUVVNjc=";
    private static final SecretKey secretKey = Keys.hmacShaKeyFor(
        Decoders.BASE64.decode(TEST_SECRET));
    private static final Long expireAccessMs = 1000L * 60 * 15; // 15분
    private static final Long expireRefreshMs = 1000L * 60 * 60 * 24 * 7; // 7일

    public TestJwtProvider() {
        super();
    }

    @Override
    public Long getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("memberId", Long.class);
    }

    @Override
    public Long getExpireAccessMs(String accessToken) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken)
            .getPayload()
            .getExpiration().getTime();
    }

    @Override
    public String createAccessToken(Long memberId, String email, SocialType socialType) {
        return Jwts.builder().claim("memberId", memberId).claim("email", email)
            .claim("socialType", socialType)
            .claim("role", "user")
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireAccessMs))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public String createRefreshToken(Long memberId, String email) {
        return Jwts.builder().claim("memberId", memberId).claim("email", email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireRefreshMs))
            .signWith(secretKey)
            .compact();
    }

    @Override
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

    @Override
    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
