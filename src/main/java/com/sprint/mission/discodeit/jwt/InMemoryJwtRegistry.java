//package com.sprint.mission.discodeit.jwt;
//
//import com.sprint.mission.discodeit.dto.data.JwtInformation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//@Component
//@RequiredArgsConstructor
//public class InMemoryJwtRegistry implements JwtRegistry {
//
//    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
//    private final int maxActiveJwtCount = 1;
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void registerJwtInformation(JwtInformation jwtInformation) {
//        if (maxActiveJwtCount == 1)
//            invalidateJwtInformationByUserId(jwtInformation.getDto().userDto().id().toString());
//
//        Queue<JwtInformation> queue = new ConcurrentLinkedQueue<>();
//        queue.add(jwtInformation);
//
//        origin.put(jwtInformation.getDto().userDto().id(), queue);
//    }
//
//    @Override
//    public void invalidateJwtInformationByUserId(String userId) {
//
//        if (origin.size() == 0
//                || origin.containsKey(UUID.fromString(userId)) == false
//                || origin.get(UUID.fromString(userId)).isEmpty())
//            return;
//
//        origin.remove(UUID.fromString(userId));
//    }
//
//    @Override
//    public void invalidateJwtInformationByRefreshToken(String refreshToken) {
//        if (hasActiveJwtInformationByRefreshToken(refreshToken) == false)
//            return;
//
//        UUID key = origin.entrySet().stream()
//                .filter(entry -> entry.getValue()
//                        .stream()
//                        .anyMatch(info -> info.getRefreshToken().equals(refreshToken)))
//                .findFirst()
//                .map(Map.Entry::getKey).orElse(null);
//
//        if (key == null)
//            return;
//
//        origin.remove(key);
//    }
//
//    @Override
//    public boolean hasActiveJwtInformationByUserId(String userId) {
//        if (origin.size() == 0 || origin.get(UUID.fromString(userId)).isEmpty())
//            return false;
//
//        return true;
//    }
//
//    @Override
//    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
//        if (origin.size() == 0)
//            return false;
//
//        return origin.values().stream().flatMap(Collection::stream)
//                .anyMatch(jwtInformation -> jwtInformation.getAccessToken().equals(accessToken));
//    }
//
//    @Override
//    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
//        if (origin.size() == 0)
//            return false;
//
//        return origin.values().stream().flatMap(Collection::stream)
//                .anyMatch(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken));
//    }
//
//    @Override
//    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
//        if (origin.size() == 0)
//            return;
//
//        UUID key = origin.entrySet().stream().filter(x -> x.getValue()
//                        .stream().anyMatch(jwt -> jwt.getRefreshToken().equals(refreshToken)))
//                .map(Map.Entry::getKey).findFirst().orElse(null);
//
//        Queue<JwtInformation> queue = new ConcurrentLinkedQueue<>();
//        queue.add(newJwtInformation);
//
//        if (key == null)
//            key = newJwtInformation.getDto().userDto().id();
//
//        origin.put(key, queue);
//    }
//
//    @Override
//    @Scheduled(fixedDelay = 1000 * 60 * 5)
//    public void clearExpiredJwtInformation() {
//        if (origin.size() == 0)
//            return;
//
//        List<UUID> keys = origin.entrySet()
//                .stream()
//                .filter(x -> x.getValue()
//                        .stream()
//                        .anyMatch(jwt -> jwtTokenProvider.validateToken(jwt.getRefreshToken()) == false))
//                .map(Map.Entry::getKey).toList();
//
//        keys.forEach(x -> origin.remove(x));
//    }
//}
