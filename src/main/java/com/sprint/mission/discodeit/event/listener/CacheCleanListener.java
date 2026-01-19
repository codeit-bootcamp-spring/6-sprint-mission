package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.CacheClearEvent;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CacheCleanListener {

  private final CacheManager defaultCacheManager;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCacheClearEvent(CacheClearEvent.RenewUserListCacheEvent event) {

    if (defaultCacheManager.getCache("userListCache") == null) {
      return;
    }

    Objects.requireNonNull(defaultCacheManager.getCache("userListCache")).clear();

  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCacheClearEvent(CacheClearEvent.RenewNotificationByUserIdCacheEvent event) {

    if (defaultCacheManager.getCache("notificationsByUserIdCache") == null) {
      return;
    }

    for (UUID userId : event.getUserIdList()) {
      Objects.requireNonNull(defaultCacheManager.getCache("notificationsByUserIdCache"))
          .evict(userId);
    }

  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCacheClearEvent(CacheClearEvent.RenewChannelListByUserIdCacheEvent event) {

    if (defaultCacheManager.getCache("channelsByUserIdCache") == null) {
      return;
    }

    for (UUID userId : event.getUserIdList()) {
      Objects.requireNonNull(defaultCacheManager.getCache("channelsByUserIdCache"))
          .evict(userId);
    }

  }

}
