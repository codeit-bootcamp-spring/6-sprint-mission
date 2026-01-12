package com.sprint.mission.discodeit.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCacheNames(
        List.of("binaryContentCache", "channelCache", "messageCache",
            "readStatusCache", "userCache"));
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .maximumSize(10000)
        // 업데이트시 cacheEvict하므로 유효기간 길게 설정
        .expireAfterWrite(1, TimeUnit.DAYS)
        .expireAfterAccess(1, TimeUnit.HOURS)
        .recordStats());
    return cacheManager;
  }
}
