package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.evaluator.GlobalPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class MethodSecurityConfig {

  private final GlobalPermissionEvaluator globalPermissionEvaluator;

  @Bean
  public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {

    DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
    defaultMethodSecurityExpressionHandler.setPermissionEvaluator(globalPermissionEvaluator);
    defaultMethodSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
    return defaultMethodSecurityExpressionHandler;

  }

  @Bean
  public RoleHierarchy roleHierarchy() {

    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy =
        "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
            "ROLE_CHANNEL_MANAGER > ROLE_USER";
    roleHierarchy.setHierarchy(hierarchy);

    return roleHierarchy;

  }

}
