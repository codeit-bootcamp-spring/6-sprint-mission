package com.sprint.mission.discodeit.service.cache;

import com.sprint.mission.discodeit.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationCacheService {
    private final CacheManager cacheManager;

    public void evictForUser(UUID userId) {
        Cache cache = cacheManager.getCache(CacheNames.USER_NOTIFICATIONS);
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
