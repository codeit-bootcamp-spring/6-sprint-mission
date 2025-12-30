package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component("User")
@RequiredArgsConstructor
public class UserPermissionEvaluator implements DomainPermissionEvaluator {

    private final UserRepository userRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String permission) {
        User user = userRepository.findById((UUID) targetId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        return user.getUsername().equals(authentication.getName());
    }
}
