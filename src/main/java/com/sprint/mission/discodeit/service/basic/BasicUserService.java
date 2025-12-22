package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
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
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    @Transactional
    @Override
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
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(null);
        String password = passwordEncoder.encode(userCreateRequest.password());
        Role role = Role.USER;

        User user = new User(username, email, password, nullableProfile, role);

        userRepository.save(user);
        log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
        return toDtoWithOnline(user, false);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.debug("사용자 조회 시작: id={}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));
        boolean online = getOnlineUserIds().contains(user.getId());
        UserDto userDto = toDtoWithOnline(user, online);
        log.info("사용자 조회 완료: id={}", userId);
        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.debug("모든 사용자 조회 시작");
        Set<UUID> onlineUserIds = getOnlineUserIds();
        List<UserDto> userDtos = userRepository.findAllWithProfile()
            .stream()
            .map(user -> toDtoWithOnline(user, onlineUserIds.contains(user.getId())))
            .toList();
        log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
        return userDtos;
    }

    @Transactional
    @Override
    @PreAuthorize("@resourceSecurity.isUserOwner(#userId, principal)")
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
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        log.info("사용자 수정 완료: id={}", userId);
        boolean online = getOnlineUserIds().contains(user.getId());
        return toDtoWithOnline(user, online);
    }

    @Transactional
    @Override
    @PreAuthorize("@resourceSecurity.isUserOwner(#userId, principal)")
    public void delete(UUID userId) {
        log.debug("사용자 삭제 시작: id={}", userId);

        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }

        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료: id={}", userId);
    }

    private Set<UUID> getOnlineUserIds() {
        return sessionRegistry.getAllPrincipals().stream()
            .filter(DiscodeitUserDetails.class::isInstance)
            .map(DiscodeitUserDetails.class::cast)
            .filter(details -> !sessionRegistry.getAllSessions(details, false).isEmpty())
            .map(details -> details.getUserDto().id())
            .collect(java.util.stream.Collectors.toSet());
    }

    private UserDto toDtoWithOnline(User user, boolean online) {
        UserDto dto = userMapper.toDto(user);
        return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(), online);
    }
}
