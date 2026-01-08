package com.sprint.mission.discodeit.event.event;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
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

    private List<UUID> userIdList;

    @Builder(access = AccessLevel.PROTECTED)
    public RenewNotificationByUserIdCacheEvent(List<UUID> userIdList) {
      this.userIdList = userIdList;
    }

    public static RenewNotificationByUserIdCacheEvent of(List<UUID> userIdList) {
      return new RenewNotificationByUserIdCacheEvent(userIdList);
    }

  }

  @Component
  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class RenewChannelListByUserIdCacheEvent {

    private List<UUID> userIdList;

    @Builder(access = AccessLevel.PROTECTED)
    public RenewChannelListByUserIdCacheEvent(List<UUID> userIdList) {
      this.userIdList = userIdList;
    }

    public static RenewChannelListByUserIdCacheEvent of(List<UUID> userIdList) {
      return new RenewChannelListByUserIdCacheEvent(userIdList);
    }
  }

}
