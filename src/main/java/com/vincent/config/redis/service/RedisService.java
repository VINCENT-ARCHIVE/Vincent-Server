package com.vincent.config.redis.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.redis.repository.RefreshTokenRepository;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.member.entity.Member;
import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import com.vincent.exception.handler.ErrorHandler;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtProvider jwtProvider;


    private static final Duration TTL = Duration.ofMinutes(10);


    /**
     * 리프레시 토큰 발급
     */
    public RefreshToken generateRefreshToken(Member member) {
        String token = jwtProvider.createRefreshToken(member.getId(), member.getEmail());
        RefreshToken refreshToken = RefreshToken.builder().memberId(member.getId())
            .refreshToken(token).build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * 리프레시 토큰 찾기
     */
    public RefreshToken findByMemberId(Long id) {
        return refreshTokenRepository.findById(id)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED));
    }

    /**
     * 리프레시 토큰 재발급
     */
    public RefreshToken regenerateRefreshToken(Member member, RefreshToken refreshToken) {
        this.delete(refreshToken);
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());
        return refreshTokenRepository.save(
            RefreshToken.builder()
                .memberId(member.getId())
                .refreshToken(newRefreshToken)
                .build()
        );
    }

    /**
     * 리프레시 토큰 삭제
     */
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public void delete(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    /**
     * Redis 데이터 삭제
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 액세스 토큰 블랙리스트 등록
     */
    public void blacklist(String accessToken) {
        Long expireAccessMs = jwtProvider.getExpireAccessMs(accessToken);
        redisTemplate.opsForValue()
            .set(accessToken, "logout", expireAccessMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 액세스 토큰 블랙리스트 확인
     */
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
    }

    /**
     * 리프레시 토큰 존재 여부 반환
     */

    public boolean exists(Long memberId) {
        return refreshTokenRepository.existsById(memberId);
    }

    /**
     * 탈취 검증
     */
    public void verifyTokenNotHijacked(RefreshToken refreshToken, String providedToken) {
        if (!refreshToken.getRefreshToken().equals(providedToken)) {
            this.delete(refreshToken);
            throw new ErrorHandler(ErrorStatus.ANOTHER_USER);
        }
    }

    /**
     * Redis에서 리스트 데이터 가져오기
     */
    public List<Object> getList(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public void addDeviceId(String deviceId, Integer isUsing) {
        redisTemplate.opsForValue().set(deviceId, String.valueOf(isUsing));
    }

    /**
     * Redis 리스트 데이터 추가
     */
    public void addToList(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * Redis 키에 TTL 설정
     */
    public void setExpire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }


    /**
     * Redis 키 존재 여부 확인
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Redis에 IoT 데이터 저장
     */
    public void saveIotData(Long deviceId, int isUsing) {
        String redisKey = "iot:" + deviceId;
        addToList(redisKey, String.valueOf(isUsing));
        setExpire(redisKey, TTL); // TTL 설정
    }

    /**
     * IoT 데이터를 기반으로 Redis에 상태 저장 및 TTL 갱신
     */
    public void updateDeviceStatus(Long deviceId, int motionStatus) {
        String redisKey = "iot:" + deviceId;

        if (motionStatus == 1) {
            // 움직임 있음: TTL 연장
            redisTemplate.opsForValue().set(redisKey, "active", TTL.toSeconds(), TimeUnit.SECONDS);
        } else if (motionStatus == 0) {
            // 움직임 없음: 상태만 저장하고 TTL 유지
            redisTemplate.opsForValue().set(redisKey, "inactive");
        }
    }

    /**
     * Redis TTL 만료 여부 확인
     */
    public boolean isTTLExpired(String redisKey) {
        return redisTemplate.opsForValue().get(redisKey) == null;
    }



}
