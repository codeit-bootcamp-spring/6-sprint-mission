package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaAuditing          // createdAt, updatedAt 자동 설정을 위한 어노테이션
@EnableScheduling
public class AppConfig {

}
