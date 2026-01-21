package com.sprint.mission.discodeit.dto.model;

import com.sprint.mission.discodeit.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationDto(
    UUID id,
    Instant createdAt,
    UUID receiverId,
    String title,
    String content
) {

  public static NotificationDto from(Notification notification) {
    return NotificationDto.builder()
        .id(notification.getId())
        .createdAt(notification.getCreatedAt())
        .receiverId(notification.getReceiverId())
        .title(notification.getTitle())
        .content(notification.getContent())
        .build();
  }
}
