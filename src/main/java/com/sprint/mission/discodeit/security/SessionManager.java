package com.sprint.mission.discodeit.security;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

  private final SessionRegistry sessionRegistry;

  public List<SessionInformation> getActiveSessionsByUserId(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails user && user.getUserDto().id().equals(userId))
        // 첫 번째 일치하는 사용자에 대한 세션 정보 반환
        .findFirst()
        .map(principal -> sessionRegistry.getAllSessions(principal, false))
        .orElse(Collections.emptyList());
  }

  public void invalidateSessionsByUserId(UUID userId) {
    List<SessionInformation> activeSessionInfos = getActiveSessionsByUserId(userId);

    if (!activeSessionInfos.isEmpty()) {
      activeSessionInfos.forEach(SessionInformation::expireNow);
      log.debug("{}개의 세션이 무효화되었습니다.", activeSessionInfos.size());
    }
  }

  public boolean hasActiveSessions(UUID userId) {
    return !getActiveSessionsByUserId(userId).isEmpty();
  }
}
