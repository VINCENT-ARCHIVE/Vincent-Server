package com.vincent.redis.repository;

import com.vincent.redis.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
