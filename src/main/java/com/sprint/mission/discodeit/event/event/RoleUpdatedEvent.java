package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.entity.enums.Role;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access =  AccessLevel.PROTECTED)
@Getter
public class RoleUpdatedEvent {

  private UUID userId;
  private Role newRole;

  public static RoleUpdatedEvent of(UUID userId, Role newRole) {
    return RoleUpdatedEvent.builder()
        .userId(userId)
        .newRole(newRole)
        .build();
  }

}
