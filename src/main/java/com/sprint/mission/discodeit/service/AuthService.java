package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final SessionRegistry sessionRegistry;

    public boolean isUserOnline(String username) {
        UserDto userDto = UserDto.builder()
                .username(username)
                .build();
        UserDetails userDetails = new DiscodeitUserDetails(userDto,null);

        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);

        return !sessions.isEmpty();
    }

}
