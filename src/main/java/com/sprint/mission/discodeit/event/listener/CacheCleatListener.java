package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.CacheClearEvent;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CacheCleatListener {

  private final CacheManager defaultCacheManager;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCacheClearEvent(CacheClearEvent.RenewUserListCacheEvent event) {

    if (defaultCacheManager.getCache("userListCache") == null) {
      return;
    }

    Objects.requireNonNull(defaultCacheManager.getCache("userListCache")).clear();

  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCacheClearEvent(CacheClearEvent.RenewNotificationByUserIdCacheEvent event) {

    if (defaultCacheManager.getCache("notificationsByUserIdCache") == null) {
      return;
    }

    Objects.requireNonNull(defaultCacheManager.getCache("notificationsByUserIdCache"))
        .evict(event.getUserId());

  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCacheClearEvent(CacheClearEvent.RenewChannelListByUserIdCacheEvent event) {

    if (defaultCacheManager.getCache("channelsByUserIdCache") == null) {
      return;
    }

    Objects.requireNonNull(defaultCacheManager.getCache("channelsByUserIdCache"))
        .evict(event.getUserId());

  }

}
