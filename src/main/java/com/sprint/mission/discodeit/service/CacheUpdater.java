package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.config.CacheNames;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
public class CacheUpdater {

  @CachePut(cacheNames = CacheNames.USER_CHANNELS, key = "#userId")
  public List<ChannelDto> putUserChannels(UUID userId, List<ChannelDto> channels) {
    return channels;
  }

  @CachePut(cacheNames = CacheNames.USER_NOTIFICATIONS, key = "#userId")
  public List<NotificationDto> putUserNotifications(UUID userId, List<NotificationDto> notifications) {
    return notifications;
  }

  @CachePut(cacheNames = CacheNames.USERS)
  public List<UserDto> putUsers(List<UserDto> users) {
    return users;
  }
}
