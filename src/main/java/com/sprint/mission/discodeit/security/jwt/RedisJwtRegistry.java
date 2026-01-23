package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.redis.RedisLockProvider.RedisLockAcquisitionException;
import com.sprint.mission.discodeit.redis.RedisLockProvider;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 서버 인스턴스 수평 확장에 대응하고자 추가됨 (미션12)
 * Refresh Token 관리 (로그아웃, 중복 로그인 방지) 위해 Registry 사용
 * 클라이언트는 RT 탈취 방지를 위해 AT 만료 시에만 RT 전송.
 */
@Slf4j
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

    private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
    private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
    private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    private static final int MAX_ACTIVE_JWT_COUNT = 3;

    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;

    @Override
    @CacheEvict(value = "users", key = "'all'")
    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void registerJwtInformation(JwtInformation jwtInformation) {
        String userKey = getUserKey(jwtInformation.getUserId());
        String lockKey = jwtInformation.getUserId().toString();

        redisLockProvider.acquireLock(lockKey);
        try {
            Long currentSize = redisTemplate.opsForList().size(userKey);

            while (currentSize != null && currentSize >= MAX_ACTIVE_JWT_COUNT) {
                Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
                if (oldestTokenObj instanceof JwtInformation oldestToken) {
                    removeTokenIndex(oldestToken.accessToken(), oldestToken.refreshToken());
                }
                currentSize = redisTemplate.opsForList().size(userKey);
            }

            redisTemplate.opsForList().rightPush(userKey, jwtInformation);
            redisTemplate.expire(userKey, DEFAULT_TTL);
            addTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }

        eventPublisher.publishEvent(new UserLogInOutEvent(jwtInformation.getUserId(), true));
    }


    @Override
    @CacheEvict(value = "users", key = "'all'")
    public void invalidateJwtInformationByUserId(UUID userId) {
        String userKey = getUserKey(userId);

        List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
        if (tokens != null) {
            tokens.forEach(tokenObj -> {
                if (tokenObj instanceof JwtInformation jwtInfo) {
                    removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
                }
            });
        }

        redisTemplate.delete(userKey);
        eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        String userKey = getUserKey(userId);
        Long size = redisTemplate.opsForList().size(userKey);
        return size != null && size > 0;
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)
        );
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
        );
    }

    @Override
    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInfo) {
        String userKey = getUserKey(newJwtInfo.getUserId());
        String lockKey = newJwtInfo.getUserId().toString();

        redisLockProvider.acquireLock(lockKey);

        try {
            // 사용자의 모든 토큰 조회
            List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

            if (tokens != null) {
                for (int i = 0; i < tokens.size(); i++) {
                    if (tokens.get(i) instanceof JwtInformation oldJwtInfo &&
                            oldJwtInfo.refreshToken().equals(refreshToken)) {

                        removeTokenIndex(oldJwtInfo.accessToken(), oldJwtInfo.refreshToken());

                        JwtInformation rotatedJwtInfo = new JwtInformation(
                                oldJwtInfo.dto(),
                                newJwtInfo.accessToken(),
                                newJwtInfo.refreshToken()
                        );

                        redisTemplate.opsForList().set(userKey, i, rotatedJwtInfo);
                        addTokenIndex(newJwtInfo.accessToken(), newJwtInfo.refreshToken());
                        redisTemplate.expire(userKey, DEFAULT_TTL);
                        log.debug("JWT 로테이션 완료: User={}, NewAccessToken={}", oldJwtInfo.getUserId(), rotatedJwtInfo.accessToken());
                        break;
                    }
                }
            }

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");

        for (String userKey : userKeys) {
            List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

            if (tokens != null) {
                boolean hasValidTokens = false;

                for (int i = tokens.size() - 1; i >= 0; i--) {
                    if (tokens.get(i) instanceof JwtInformation jwtInfo) {
                        boolean isExpired =
                                !jwtTokenProvider.validateAccessToken(jwtInfo.accessToken()) ||
                                        !jwtTokenProvider.validateRefreshToken(jwtInfo.refreshToken());

                        if (isExpired) {
                            redisTemplate.opsForList().set(userKey, i, "EXPIRED");
                            redisTemplate.opsForList().remove(userKey, 1, "EXPIRED");
                            removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
                        } else {
                            hasValidTokens = true;
                        }
                    }
                }

                if (!hasValidTokens) {
                    redisTemplate.delete(userKey);
                }
            }
        }
    }

    /**
     * 헬퍼 메서드들
     */
    private String getUserKey(UUID userId) {
        return USER_JWT_KEY_PREFIX + userId.toString();
    }

    private void addTokenIndex(String accessToken, String refreshToken) {
        // Set에 토큰 추가 (add: 중복되면 무시됨)
        redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, accessToken);
        redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, refreshToken);

        // 인덱스 키에도 만료 시간 설정 (메모리 누수 방지)
        redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, DEFAULT_TTL);
        redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, DEFAULT_TTL);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        // Set에서 토큰 제거
        redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, accessToken);
        redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refreshToken);
    }
}