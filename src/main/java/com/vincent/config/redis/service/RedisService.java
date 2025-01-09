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
    // 테스트용 getter 메서드 추가
    @Getter
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;
    @Getter
    private final IotDataService iotDataService;
    @Getter
    private final SocketDataService socketDataService;


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

    public void saveIotData(Long deviceId, int isUsing) {
        String redisKey = "iot:" + deviceId;

        // Redis에 state 값을 String으로 저장
        redisTemplate.opsForList().rightPush(redisKey, String.valueOf(isUsing));
        redisTemplate.expire(redisKey, Duration.ofMinutes(10)); // TTL 설정
    }

    @Transactional
    public boolean updateIsSocketUsing(Long deviceId) {
        String redisKey = "iot:" + deviceId;

        // Redis에서 데이터 읽기
        List<Object> rawStates = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (rawStates == null || rawStates.size() < 60) {
            return false; // 10분 데이터가 누적되지 않음
        }
        // String 데이터를 Integer로 변환
        List<Integer> states = rawStates.stream()
            .map(state -> Integer.valueOf((String) state)) // String을 Integer로 변환
            .collect(Collectors.toList());

        // 0과 1 개수 계산
        long countZero = states.stream().filter(s -> s == 0).count();
        long countOne = states.size() - countZero;

        // 업데이트할 상태 결정
        boolean isUsing = countOne > countZero;

        // socket 테이블 업데이트
        Iot targetIot = iotDataService.findByDeviceId(deviceId);
        Socket targetSocket = socketDataService.findById(targetIot.getSocket().getId());

        targetSocket.setIsUsing(isUsing);
        socketDataService.save(targetSocket);


        // Redis 데이터 초기화 (처리 완료 후 삭제)
        redisTemplate.delete(redisKey);

        return true; // 업데이트 성공
    }

}
