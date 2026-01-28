package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
//    @Bean
//    public CaffeineCacheManager caffeineCacheManager() {
//        CaffeineCacheManager manager = new CaffeineCacheManager("channels", "notifications", "users");
//        manager.setCaffeine(
//                Caffeine.newBuilder()
//                        .initialCapacity(100)
//                        .maximumSize(1000)
//                        .expireAfterAccess(10, TimeUnit.MINUTES)
//                        .recordStats()
//        );
//
//        return manager;
//    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                ).prefixCacheNameWith("discodeit:")
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("users", config.entryTtl(Duration.ofMinutes(3)));
        configs.put("channels", config);
        configs.put("notifications", config);


        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(configs)
                .build();

    }
}
