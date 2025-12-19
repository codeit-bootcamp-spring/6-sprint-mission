package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing          // createdAt, updatedAt 자동 설정을 위한 어노테이션
public class JpaAuditingConfig {

}
