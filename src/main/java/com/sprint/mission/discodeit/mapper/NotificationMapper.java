package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.notification.NotificationResponseDto;
import com.sprint.mission.discodeit.entity.Notification;

public class NotificationMapper {

    public static NotificationResponseDto toDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .receiverId(notification.getUser().getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
