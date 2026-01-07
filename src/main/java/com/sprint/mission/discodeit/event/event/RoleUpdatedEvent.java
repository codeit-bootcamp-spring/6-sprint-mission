package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.entity.enums.Role;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoleUpdatedEvent {

  private UUID userId;
  private Role newRole;

  @Builder(access = AccessLevel.PROTECTED)
  private RoleUpdatedEvent(UUID userId, Role newRole) {
    this.userId = userId;
    this.newRole = newRole;
  }

  public static RoleUpdatedEvent of(UUID userId, Role newRole) {
    return RoleUpdatedEvent.builder()
        .userId(userId)
        .newRole(newRole)
        .build();
  }

}
