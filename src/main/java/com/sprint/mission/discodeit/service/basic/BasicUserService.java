package com.sprint.mission.discodeit.service.basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.jwt.JwtRegistry;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CachedUserService cachedUserService;
    private final ObjectMapper objectMapper;
    private final SseService sseService;

    @Transactional
    @Override
    @CacheEvict(
            value = "users",
            allEntries = true
    )
    public UserDto create(UserCreateRequest userCreateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.debug("사용자 생성 시작: {}", userCreateRequest);

        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException.withEmail(email);
        }
        if (userRepository.existsByUsername(username)) {
            throw UserAlreadyExistsException.withUsername(username);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    return binaryContent;
                })
                .orElse(null);
        String password = passwordEncoder.encode(userCreateRequest.password());

        User user = new User(username, email, password, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);
        user.updateRole(Role.ROLE_USER);
        userRepository.save(user);
        log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);

        if (nullableProfile != null)
            applicationEventPublisher.publishEvent(new BinaryContentCreatedEvent(user.getId(),
                    nullableProfile.getId(),
                    optionalProfileCreateRequest.get().bytes()));

        UserDto userDto = userMapper.toDto(user);
        sendSSe("users.created", userDto);

        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.debug("사용자 조회 시작: id={}", userId);
        UserDto userDto = userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        log.info("사용자 조회 완료: id={}", userId);
        return userDto;
    }

    @Override
    public List<UserDto> findAll() {
        String result = cachedUserService.findAll();
        if (result.isEmpty()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(result, new TypeReference<List<UserDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new UserException(ErrorCode.INVALID_REQUEST);
        }
    }

    @Transactional
    @Override
    @PreAuthorize("#userId == @UserCheck.getUserId(authentication)")
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    UserNotFoundException exception = UserNotFoundException.withId(userId);
                    return exception;
                });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (userRepository.existsByEmail(newEmail)) {
            throw UserAlreadyExistsException.withEmail(newEmail);
        }

        if (userRepository.existsByUsername(newUsername)) {
            throw UserAlreadyExistsException.withUsername(newUsername);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    applicationEventPublisher.publishEvent(new BinaryContentCreatedEvent(userId, binaryContent.getId(), bytes));
                    return binaryContent;
                })
                .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        log.info("사용자 수정 완료: id={}", userId);
        UserDto userDto = userMapper.toDto(user);
        sendSSe("users.updated", userDto);

        return userDto;
    }

    @Transactional
    @Override
    @PreAuthorize("#userId == @UserCheck.getUserId(authentication)")
    @CacheEvict(
            value = "users",
            allEntries = true
    )
    public void delete(UUID userId) {
        log.debug("사용자 삭제 시작: id={}", userId);

        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }

        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료: id={}", userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));
        log.info("사용자 role 변경. {} -> {}", user.getRole(), request.newRole());

        Role oldRole = user.getRole();

        user.updateRole(request.newRole());
        userRepository.save(user);

        jwtRegistry.invalidateJwtInformationByUserId(user.getId().toString());

        applicationEventPublisher.publishEvent(new RoleUpdatedEvent(user.getId(),
                "권한이 변경되었습니다.",
                String.format("%s -> %s", oldRole, request.newRole())));

        UserDto userDto = userMapper.toDto(user);
        sendSSe("users.deleted", userDto);

        return userDto;
    }

    @Override
    public List<UserDto> findAllByRole(Role role) {
        return userRepository.findAllByRole(role).orElse(null)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    private void sendSSe(String name, UserDto userDto) {
        sseService.send(List.of(userDto.id()), name, userDto);
    }
}
