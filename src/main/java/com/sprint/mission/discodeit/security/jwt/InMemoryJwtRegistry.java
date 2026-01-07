package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
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

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;
    private final JwtTokenProvider jwtTokenProvider;

    private final long CLEAR_CYCLE = 1000 * 60 * 5;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        origin.computeIfAbsent(userId, k -> new LinkedList<>()); //Value가 이미 있으면 기존 값 반환

        if (origin.get(userId).size() >= maxActiveJwtCount) {
            origin.get(userId).poll();
        }

        if (!origin.get(userId).offer(jwtInformation)) {
            log.error("JwtInformation cannot be inserted.");
            throw new JwtException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteJwtInformationByRefreshToken(String refreshToken) {
        origin.values().forEach(queue ->
                queue.removeIf(jwtInformation ->
                        jwtInformation.getRefreshToken().equals(refreshToken)
                )
        );
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        if (origin.get(userId) == null) {
            return false;
        }
        Queue<JwtInformation> queue = origin.get(userId);

        for (JwtInformation jwtInformation : queue) {
            if (jwtTokenProvider.validateToken(jwtInformation.getAccessToken())) {
                return true;
            }
        }

        return !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(jwtInformation ->
                        jwtInformation.getAccessToken().equals(accessToken) &&
                                jwtTokenProvider.validateToken(jwtInformation.getAccessToken()));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(jwtInformation ->
                        jwtInformation.getRefreshToken().equals(refreshToken) &&
                                jwtTokenProvider.validateToken(jwtInformation.getRefreshToken()));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.values().stream().flatMap(Collection::stream)
                .filter(jwtInformation ->
                        jwtInformation.getRefreshToken().equals(refreshToken))
                .forEach(jwtInformation ->
                        jwtInformation.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken()));
    }

    @Scheduled(fixedDelay = CLEAR_CYCLE)
    @Override
    public void clearExpiredJwtInformation() {
        origin.forEach((userId, queue) ->
                queue.removeIf(jwtInformation
                        -> !jwtTokenProvider.validateToken(jwtInformation.getRefreshToken()))
        );

        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
