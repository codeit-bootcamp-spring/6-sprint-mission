package com.sprint.mission.discodeit.security.evaluator;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface DomainPermissionEvaluator {
    boolean hasPermission(Authentication authentication, UUID targetId, String permission);
}
