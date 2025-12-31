package com.sprint.mission.discodeit.security.evaluator;

import java.io.Serializable;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalPermissionEvaluator implements PermissionEvaluator {

  private final Map<String, DomainPermissionEvaluator> evaluatorMap;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
    return false;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

    DomainPermissionEvaluator evaluator = evaluatorMap.get(targetType);

    if (evaluator == null) {
      throw new IllegalArgumentException("No evaluator found for target type: " + targetType);
    }

    return evaluator.hasPermission(authentication, targetId, (String) permission);

  }
}
