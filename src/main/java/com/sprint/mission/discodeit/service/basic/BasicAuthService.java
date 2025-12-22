package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto changeRole(RoleUpdateRequest roleUpdateRequest) {
        User user = userRepository.findById(roleUpdateRequest.userId())
            .orElseThrow(UserNotFoundException::new);

        user.updateRole(roleUpdateRequest.newRole());
        expireUserSessions(user.getId());
        return userMapper.toDto(user);
    }

    private void expireUserSessions(UUID userId) {
        sessionRegistry.getAllPrincipals().stream()
            .filter(DiscodeitUserDetails.class::isInstance)
            .map(DiscodeitUserDetails.class::cast)
            .filter(details -> userId.equals(details.getUserDto().id()))
            .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
            .forEach(SessionInformation::expireNow);
    }
}
