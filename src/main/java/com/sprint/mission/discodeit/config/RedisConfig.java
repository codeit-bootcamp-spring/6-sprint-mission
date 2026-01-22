package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       @Qualifier("redisSerializer") GenericJackson2JsonRedisSerializer redisSerializer) {
        // Redis 연결을 위한 팩토리 설정
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 직렬화 - 가독성을 위해 문자열로
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 직렬화 - JSON으로
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);

        // 빈 설정이 완료되었음을 알리고 초기화 수행
        template.afterPropertiesSet();

        return template;
    }

    @Bean("redisSerializer")
    public GenericJackson2JsonRedisSerializer redisSerializer(ObjectMapper objectMapper) {

        // Spring의 ObjectMapper 설정을 복사, Redis 전용으로 설정
        ObjectMapper redisObjectMapper = objectMapper.copy();

        // 다형성 처리를 위한 타입 정보 포함 설정
        // 역직렬화 시 JSON을 어떤 객체(타입)로 매핑할지 정보 필요하므로 사용
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance, // 검증기 설정 - 아무나 원래 타입을 조작하지 못하게 함
                ObjectMapper.DefaultTyping.NON_FINAL, // 타입 정보 저장 범위 - final이 아닌 클래스들의 타입 저장
                As.PROPERTY // 타입 정보를 JSON 속성(@class)으로 포함
        );
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }
}
