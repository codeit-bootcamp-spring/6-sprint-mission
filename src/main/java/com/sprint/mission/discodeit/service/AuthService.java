package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Auth.LoginRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.auth.AuthLoginFailException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.username())
                .orElseThrow(AuthLoginFailException::new);

        if (!user.getPassword().equals(loginRequest.password())) {
            log.warn("로그인 실패 발생: userId={}",user.getId());
            throw new AuthLoginFailException();
        }

        //로그인시 오프라인 -> 온라인으로
        user.getStatus().update(Instant.now());
        userStatusRepository.save(user.getStatus());
        return userMapper.toDto(user);
    }
}
