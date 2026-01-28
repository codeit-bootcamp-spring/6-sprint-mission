package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.dto.UserDTO;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserUpdatedEvent {

  private UUID receiverId;
  private UserNotificationType type;
  private UserDTO.User data;

  @Getter
  public enum UserNotificationType {
    CREATED("users.created"),
    UPDATED("users.updated"),
    DELETED("users.deleted");

    private final String eventName;

    UserNotificationType(String eventName) {
      this.eventName = eventName;
    }

  }

  @Builder(access = AccessLevel.PROTECTED)
  public UserUpdatedEvent(UUID receiverId, UserNotificationType type, UserDTO.User data) {
    this.receiverId = receiverId;
    this.type = type;
    this.data = data;
  }

  public static UserUpdatedEvent of(UUID receiverId, UserNotificationType type,
      UserDTO.User data) {
    return UserUpdatedEvent.builder()
        .receiverId(receiverId)
        .type(type)
        .data(data)
        .build();
  }

}
