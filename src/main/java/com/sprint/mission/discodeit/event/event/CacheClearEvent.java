package com.sprint.mission.discodeit.event.event;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CacheClearEvent {

  @Component
  public static class RenewUserListCacheEvent {
  }

  @Component
  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class RenewNotificationByUserIdCacheEvent {

    private UUID userId;

    public static RenewNotificationByUserIdCacheEvent of(UUID userId) {
      return new RenewNotificationByUserIdCacheEvent();
    }

  }

  @Component
  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class RenewChannelListByUserIdCacheEvent {

    private UUID userId;

    public static RenewChannelListByUserIdCacheEvent of(UUID userId) {
      return new RenewChannelListByUserIdCacheEvent();
    }
  }

}
