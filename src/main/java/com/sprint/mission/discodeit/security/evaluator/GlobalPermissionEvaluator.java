package com.sprint.mission.discodeit.security.evaluator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalPermissionEvaluator implements PermissionEvaluator {
    private final Map<String, DomainPermissionEvaluator> permissionEvaluators;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        DomainPermissionEvaluator permissionEvaluator = permissionEvaluators.get(targetType);
        if (permissionEvaluator == null) {
            log.warn("{} is not a valid targetType", targetType);
            return false;
        }
        UUID targetUuid = (UUID) targetId;
        return permissionEvaluator.hasPermission(authentication, targetUuid, (String) permission);
    }
}
