package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

/*    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Transactional
    public UserResponseDto login(LoginRequestDto request){

        // FETCH JOIN으로 UserStatus도 한번에 가져옴
        User user = userRepository.findByUsernameWithStatusAndProfile(request.username())
                .orElseThrow(() -> new UserNotFoundException(request.username()));

        if (!user.getPassword().equals(request.password())){
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getUserStatus() == null) {
            log.info("해당 유저에 대해 UserStatus가 존재하지 않아 새로 생성합니다: " + user.getUsername());
            UserStatus newUserStatus = UserStatus.builder()
                    .user(user)
                    .lastActiveAt(Instant.now())
                    .build();
            log.info("UserStatus를 생성했습니다: " + newUserStatus.getId());
            user.setUserStatus(newUserStatus);
        } else {
            user.getUserStatus().updateLastActiveAt(Instant.now());
        }

        log.info("로그인 되었습니다: " +  user.getId());
        BinaryContentResponseDto profileImage = binaryContentMapper.toDto(user.getProfileImage());
        return userMapper.toDto(user);
    }*/
}
