package com.sprint.mission.discodeit.security.evaluator;

import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.Serializable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("User")
@RequiredArgsConstructor
public class UserPermissionEvaluator implements DomainPermissionEvaluator{

  private final UserRepository userRepository;

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String permission) {

    UserEntity user = userRepository.findById((UUID) targetId)
        .orElseThrow(NoSuchUserException::new);

    boolean isAdmin = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"));
    if (isAdmin) return true;

    return user.getUsername().equals(authentication.getName());

  }
}
