package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public void userMatch(LoginDto loginDto) {
        boolean user = userRepository.existsByUsername(loginDto.username());
        if (!user) {
            throw new IllegalArgumentException("Incorrect password");
        }
    }
}