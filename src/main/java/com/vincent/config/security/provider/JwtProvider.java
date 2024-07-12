package com.vincent.config.security.provider;

import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
  private static final Long expireMs = 1000L * 60; //30분

  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println(secret);
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String getMemberId(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("memberId", String.class);
  }

  public String getEmail(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("email", String.class);
  }

  public String getRole(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  public Boolean isExpired(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
  }

  public String createJwt(Long memberId, String email) {
    return Jwts.builder()
        .claim("memberId", memberId)
        .claim("email", email)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expireMs))
        .signWith(secretKey)
        .compact();
  }

  public String createJwt(String email) {
    return Jwts.builder()
        .claim("email", email)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expireMs))
        .signWith(secretKey)
        .compact();
  }

  public void validateAccessToken(String accessToken) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(accessToken)
          .getPayload()
          .getExpiration();
    } catch (ExpiredJwtException e) {
      throw new JwtExpiredHandler("Expired Token Exception");
    } catch (UnsupportedJwtException | SecurityException | MalformedJwtException |
             NullPointerException e) {
      throw new JwtInvalidHandler("Invalid Token Exception");
    }
  }

}
