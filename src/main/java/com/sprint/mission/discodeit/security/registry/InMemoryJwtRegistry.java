package com.sprint.mission.discodeit.security.registry;

import com.sprint.mission.discodeit.entity.JwtInformation;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry{

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;
  private final JwtTokenProvider jwtTokenProvider;


  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {

    UUID key = jwtInformation.getUser().getId();

    if (origin.containsKey(key)){
      Queue<JwtInformation> queue = origin.get(key);
      if (queue.size() >= maxActiveJwtCount){
        queue.poll();
      }
      queue.offer(jwtInformation);
    } else {
      Queue<JwtInformation> queue = new ConcurrentLinkedDeque<>();
      queue.offer(jwtInformation);
      origin.put(key, queue);
    }

  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {

    for (Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
      for  (JwtInformation jwtInformation : entry.getValue()) {
        if (jwtInformation.getAccessToken().equals(accessToken)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {

    for (Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
      for  (JwtInformation jwtInformation : entry.getValue()) {
        if (jwtInformation.getRefreshToken().equals(refreshToken)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {

    for (Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
      for (JwtInformation jwtInformation : entry.getValue()) {
        if (jwtInformation.getRefreshToken().equals(refreshToken)) {
          entry.getValue().remove(jwtInformation);
          entry.getValue().offer(newJwtInformation);
          return;
        }
      }
    }

  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {

    for (Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
      Queue<JwtInformation> queue = entry.getValue();
      queue.removeIf(jwtInformation ->
          !jwtTokenProvider.validateRefreshToken(jwtInformation.getRefreshToken())
      );
    }

  }
}
