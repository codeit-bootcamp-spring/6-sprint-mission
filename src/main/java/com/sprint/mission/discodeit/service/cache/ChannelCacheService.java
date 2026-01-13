package com.sprint.mission.discodeit.service.cache;

import com.sprint.mission.discodeit.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelCacheService {
    private final CacheManager cacheManager;

    public void evictForUsers(List<UUID> userIds) {
        Cache cache = cacheManager.getCache(CacheNames.USER_CHANNELS);
        if  (cache != null) {
            for (UUID userId : userIds) {
                cache.evict(userId);
            }
        }
    }

    public void evictAll() {
        Cache cache = cacheManager.getCache(CacheNames.USER_CHANNELS);
        if (cache != null) {
            cache.clear();
        }
    }
}
