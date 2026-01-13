package com.sprint.mission.discodeit.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  @ConditionalOnProperty(
      prefix = "discodeit.cache",
      name = "type",
      havingValue = "caffeine"
  )
  public CacheManager caffeineCacheManager() {
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

  @Bean
  @ConditionalOnProperty(
      prefix = "discodeit.cache",
      name = "type",
      havingValue = "redis"
  )
  public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

    ObjectMapper redisObjectMapper = objectMapper.copy();
    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        DefaultTyping.EVERYTHING,
        As.PROPERTY
    );

    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
            )
        )
        .prefixCacheNameWith("discodeit:")
        .entryTtl(Duration.ofDays(1))
        .disableCachingNullValues();

    Set<String> cacheNames = Set.of(
        "binaryContentCache", "channelCache", "messageCache",
        "readStatusCache", "userCache"
    );

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .initialCacheNames(cacheNames)
        .enableStatistics()
        .build();
  }
}
