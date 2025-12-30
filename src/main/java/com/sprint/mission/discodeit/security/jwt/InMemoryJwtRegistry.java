package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry {

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<UUID, Queue<JwtInformation>> registry = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getDto().id();

        // compute: 원자적 연산을 통해 동시성 보장하며 데이터 갱신
        registry.compute(userId, (key, existingQueue) -> {
            Queue<JwtInformation> queue = (existingQueue != null) ? existingQueue : new LinkedList<>();

            // 동시 로그인 제한 로직 (기존 세션 무효화)
            while (queue.size() >= maxActiveJwtCount) {
                queue.poll();
            }

            queue.offer(jwtInformation);
            return queue;
        });
        log.debug("Registered JWT for user: {}, Total active sessions: {}", userId, registry.get(userId).size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        registry.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return registry.containsKey(userId) && !registry.get(userId).isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return registry.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> info.getAccessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return registry.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getDto().id();
        Queue<JwtInformation> queue = registry.get(userId);

        if (queue != null) {
            // 기존 토큰 제거 후 새 토큰 정보 삽입
            queue.removeIf(info -> info.getRefreshToken().equals(oldRefreshToken));
            queue.offer(newJwtInformation);
        }
    }

    @Override
    public void clearExpiredJwtInformation() {
        registry.forEach((userId, queue) -> {
            queue.removeIf(info -> !jwtTokenProvider.validateToken(info.getRefreshToken()));
            if (queue.isEmpty()) {
                registry.remove(userId);
            }
        });
        log.debug("Cleanup task: Expired JWTs cleared from memory.");
    }
}
