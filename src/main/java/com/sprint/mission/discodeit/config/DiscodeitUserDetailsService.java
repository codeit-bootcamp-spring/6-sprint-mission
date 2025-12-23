package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService {

    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        return null;
    }

}
