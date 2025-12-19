package com.sprint.mission.discodeit.security.evaluator;

import java.io.Serializable;
import org.springframework.security.core.Authentication;

public interface DomainPermissionEvaluator {
  boolean hasPermission(Authentication authentication, Serializable targetId, String permission);
}
