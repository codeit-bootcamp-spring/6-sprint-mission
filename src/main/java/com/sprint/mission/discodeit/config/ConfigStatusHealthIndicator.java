package com.sprint.mission.discodeit.config;

import org.springframework.boot.actuate.health.Health;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

@Component
public class ConfigStatusHealthIndicator implements HealthIndicator {

    private Environment environment;

    @Override
    public Health health() {
        if (!environment.getProperty( "spring.profiles.active" ).equals( "dev" )) {
            return Health.down().withDetail("reason", "중요 설정 로드에 실패했습니다.").build();
        }
        return Health.up().withDetail("status", "모든 중요 설정이 정상적으로 로드되었습니다.").build();
    }
}
