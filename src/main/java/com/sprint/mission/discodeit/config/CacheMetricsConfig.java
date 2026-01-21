package com.sprint.mission.discodeit.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheMetricsConfig {

    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void bindCacheMetrics() {
        cacheManager.getCacheNames().forEach(this::accept);
    }

    private void accept(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            CaffeineCacheMetrics.monitor(meterRegistry, caffeineCache.getNativeCache(), cacheName);
        }
    }
}
