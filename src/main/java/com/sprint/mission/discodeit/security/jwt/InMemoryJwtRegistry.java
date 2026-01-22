package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry {

    private final JwtTokenProvider jwtTokenProvider;
    private static final int MAX_ACTIVE_JWT_COUNT = 1;

    // 사용자별 JWT 관리 - MAX_ACTIVE_JWT_COUNT를 늘릴 경우를 고려해 Queue 사용.
    private final Map<UUID, Queue<JwtInformation>> userRegistry = new ConcurrentHashMap<>();

    // 조회 속도 향상 위한 Map들
    private final Map<String, JwtInformation> accessTokenIndex = new ConcurrentHashMap<>();
    private final Map<String, JwtInformation> refreshTokenIndex = new ConcurrentHashMap<>();

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserId();

        // compute: 원자적 연산으로 동시성을 보장하면서 데이터 갱신
        userRegistry.compute(userId, (key, existingQueue) -> {
            Queue<JwtInformation> queue = (existingQueue != null) ? existingQueue : new LinkedList<>();

            // 동시 로그인 제한
            while (queue.size() >= MAX_ACTIVE_JWT_COUNT) {
                queue.poll();
            }

            queue.offer(jwtInformation);
            return queue;
        });
        accessTokenIndex.put(jwtInformation.accessToken(), jwtInformation);
        refreshTokenIndex.put(jwtInformation.refreshToken(), jwtInformation);
        log.debug("Registered JWT for user: {}, Total active sessions: {}", userId, userRegistry.get(userId).size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> userTokens = userRegistry.remove(userId);

        if (userTokens != null) {
            for (JwtInformation jwtInformation : userTokens) {
                accessTokenIndex.remove(jwtInformation.accessToken());
                refreshTokenIndex.remove(jwtInformation.refreshToken());
            }
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return userRegistry.containsKey(userId) && !userRegistry.get(userId).isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessTokenIndex.containsKey(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndex.containsKey(refreshToken);
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserId();
        JwtInformation oldJwtInformation = refreshTokenIndex.remove(oldRefreshToken);

        if (oldJwtInformation != null) {
            // 기존 토큰 제거
            accessTokenIndex.remove(oldJwtInformation.accessToken());
            Queue<JwtInformation> queue = userRegistry.get(userId);
            if (queue != null) {
                queue.remove(oldJwtInformation);
            }
        }
        // 새 토큰 저장
        registerJwtInformation(newJwtInformation);
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {

        log.debug("Cleanup task started: Clearing expired JWTs...");

        accessTokenIndex.values().forEach(info -> {
            if (!jwtTokenProvider.validateAccessToken(info.accessToken()) ||
                    !jwtTokenProvider.validateRefreshToken(info.refreshToken())) {
                removeJwtInformation(info);
            }
        });

        // 비어있는 큐(유저) 정리
        userRegistry.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        log.debug("Cleanup task: Expired JWTs cleared from memory.");
    }

    // 중복 로직을 방지하기 위한 공통 삭제 메서드
    private void removeJwtInformation(JwtInformation info) {
        accessTokenIndex.remove(info.accessToken());
        refreshTokenIndex.remove(info.refreshToken());

        Queue<JwtInformation> queue = userRegistry.get(info.getUserId());
        if (queue != null) {
            queue.remove(info);
        }
    }
}
