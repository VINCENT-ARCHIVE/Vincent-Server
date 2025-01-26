package com.vincent.config.redis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.redis.entity.RefreshToken;
import com.vincent.config.redis.repository.RefreshTokenRepository;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.iot.entity.Iot;
import com.vincent.domain.iot.service.data.IotDataService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import com.vincent.exception.handler.ErrorHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOperations;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
    }


    @Test
    void 리프레시토큰생성() {
        //given
        String token = "test";

        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test")
            .build();
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();
        //when
        when(jwtProvider.createRefreshToken(member.getId(), member.getEmail())).thenReturn(token);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        RefreshToken result = redisService.generateRefreshToken(member);

        //then
        Assertions.assertEquals(result, refreshToken);
    }

    @Test
    void 사용자Id로토큰찾기() {
        //when
        Long memberId = 1L;
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test")
            .build();

        //given
        when(refreshTokenRepository.findById(memberId)).thenReturn(Optional.of(refreshToken));
        RefreshToken result = redisService.findByMemberId(memberId);

        //then
        Assertions.assertEquals(result, refreshToken);
    }

    @Test
    void 사용자Id로토큰찾기_실패() {
        //given
        Long memberId = 1L;

        //when
        when(refreshTokenRepository.findById(memberId)).thenReturn(Optional.empty());

        //then
        ErrorHandler thrown = assertThrows(ErrorHandler.class,
            () -> redisService.findByMemberId(memberId));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED);
    }

    @Test
    void 토큰재발급() {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test")
            .build();
        RefreshToken newRefreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test2")
            .build();
        Member member = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .build();

        //when
        doNothing().when(refreshTokenRepository).delete(refreshToken);
        when(jwtProvider.createRefreshToken(member.getId(), member.getEmail())).thenReturn("test2");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(newRefreshToken);
        RefreshToken result = redisService.regenerateRefreshToken(member, refreshToken);

        //then
        Assertions.assertEquals(result.getMemberId(), newRefreshToken.getMemberId());
        Assertions.assertEquals(result.getRefreshToken(), newRefreshToken.getRefreshToken());
    }

    @Test
    void 리프레시토큰으로삭제() {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test")
            .build();

        //when
        doNothing().when(refreshTokenRepository).delete(refreshToken);
        redisService.delete(refreshToken);

        //then
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    @Test
    void 사용자Id로삭제() {
        //given
        Long memberId = 1L;

        //when
        doNothing().when(refreshTokenRepository).deleteByMemberId(memberId);
        redisService.delete(memberId);

        //then
        verify(refreshTokenRepository, times(1)).deleteByMemberId(memberId);
    }

    @Test
    void 액세스토큰블랙리스트에등록() {
        // given
        String accessToken = "test";
        Long expireAccessMs = 60000L;
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);

        // when
        when(jwtProvider.getExpireAccessMs(accessToken)).thenReturn(expireAccessMs);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisService.blacklist(accessToken);

        // then
        verify(valueOperations, times(1)).set(eq(accessToken), eq("logout"), eq(expireAccessMs),
            eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void 액세스토큰블랙리스트검증() {
        //given
        String accessToken = "test";

        //when
        when(redisTemplate.hasKey(accessToken)).thenReturn(true);
        boolean blacklisted = redisService.isBlacklisted(accessToken);

        //then
        Assertions.assertEquals(blacklisted, true);
        verify(redisTemplate, times(1)).hasKey(accessToken);

    }

    @Test
    void 사용자Id로토큰존재확인() {
        // given
        Long memberId = 1L;
        when(refreshTokenRepository.existsById(memberId)).thenReturn(true);

        // when
        boolean result = redisService.exists(memberId);

        // then
        assertTrue(result);
        verify(refreshTokenRepository, times(1)).existsById(memberId);
    }

    @Test
    void 탈취검증_위험없음() {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test")
            .build();
        String providedToken = "test";

        //when/then
        assertDoesNotThrow(() -> redisService.verifyTokenNotHijacked(refreshToken, providedToken));
    }

    @Test
    void 탈취검증_위험있음() {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
            .memberId(1L)
            .refreshToken("test")
            .build();
        String providedToken = "test2";

        //when
        ErrorHandler thrown = assertThrows(ErrorHandler.class,
            () -> redisService.verifyTokenNotHijacked(refreshToken, providedToken));

        //then
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.ANOTHER_USER);
    }

    @Test
    void 레디스리스트데이터가져오기() {
        // Given
        String key = "iot:1";
        List<Object> expectedList = List.of("0", "1", "1");

        // When
        when(listOperations.range(key, 0, -1)).thenReturn(expectedList);

        List<Object> result = redisService.getList(key, 0, -1);

        // Then
        assertEquals(expectedList, result);
    }

    @Test
    void 레디스리스트데이터추가() {
        // Given
        String key = "iot:1";
        String value = "1";

        // When
        redisService.addToList(key, value);

        // Then
        verify(listOperations, times(1)).rightPush(key, value);
    }

    @Test
    void 레디스TTL설정() {
        // Given
        String key = "iot:1";
        Duration duration = Duration.ofMinutes(10);

        // When
        redisService.setExpire(key, duration);

        // Then
        verify(redisTemplate, times(1)).expire(eq(key), eq(duration));
    }

    @Test
    void 레디스데이터삭제() {
        // Given
        String key = "iot:1";

        // When
        redisService.delete(key);

        // Then
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void 레디스키존재확인() {
        // Given
        String key = "iot:1";

        // When
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean exists = redisService.hasKey(key);

        // Then
        assertTrue(exists);
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    void 레디스에IOT데이터저장() {
        // Given
        Long deviceId = 1L;
        String redisKey = "iot:" + deviceId;
        int isUsing = 1;

        // When
        redisService.saveIotData(deviceId, isUsing);

        // Then
        verify(listOperations, times(1)).rightPush(redisKey, String.valueOf(isUsing));
        verify(redisTemplate, times(1)).expire(eq(redisKey), eq(Duration.ofMinutes(10)));
    }
}
