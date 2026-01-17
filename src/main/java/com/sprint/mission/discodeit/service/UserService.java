package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.UserRoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.principal.DiscodeitUserDetails;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;

    private static final String TEMP_FILE_PREFIX = "binary_";
    private static final String TEMP_FILE_EXTENSION = ".tmp";

    // 유저 생성
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDto create(UserCreateRequestDto request,
                                  BinaryContentCreateRequestDto profileImageRequest) {

        if (userRepository.existsByUsername(request.username())){
            throw UserAlreadyExistsException.byUsername(request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.email(), request.username(), encodedPassword);

        if (profileImageRequest != null) {
            saveProfileImage(profileImageRequest, user);
        }
        userRepository.save(user);
        log.info("회원가입이 완료되었습니다. id=" + user.getId());
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Cacheable("users")
    public List<UserResponseDto> findAll(){
        List<User> users = userRepository.findAllWithStatusAndProfile(); // N+1 문제 해결 위해 fetch join 쿼리 사용
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    // 수정
    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDto update(UUID userId, UserUpdateRequestDto request,
                                  BinaryContentCreateRequestDto profileImageRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newEmail = request.newEmail();
        String newUsername = request.newUsername();
        String newPassword = request.newPassword();

        if (newEmail != null) {
            userRepository.findByEmail(newEmail)
                    .filter(existingUser -> !existingUser.getEmail().equals(user.getEmail()))
                    .ifPresent(existingUser -> {
                        throw new UserAlreadyExistsException(newEmail);
                    });
        }

        if (newUsername != null) {
            userRepository.findByUsername(request.newUsername())
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .ifPresent(existingUser -> {
                        throw UserAlreadyExistsException.byUsername(newUsername);
                    });
        }

        if (newPassword != null) {
            newPassword = passwordEncoder.encode(newPassword);
        }

        user.update(newEmail, newUsername, newPassword);

        if (profileImageRequest != null) {
            saveProfileImage(profileImageRequest, user);
        }

        userRepository.save(user); // 명시적 저장
        log.info("사용자 수정이 완료되었습니다. id=" + user.getId());

        return userMapper.toDto(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDto updateUserRole(UserRoleUpdateRequest request) {

        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Role oldRole = user.getRole();
        user.updateRole(request.newRole());
        userRepository.save(user);
        jwtRegistry.invalidateJwtInformationByUserId(userId);

        eventPublisher.publishEvent(new UserRoleUpdatedEvent(user, oldRole, request.newRole()));
        return userMapper.toDto(user);
    }

    // 유저 삭제
    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    @CacheEvict(value = "users", allEntries = true)
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);
        log.info("사용자 삭제가 완료되었습니다. id=" + userId);
    }

    private void saveProfileImage(BinaryContentCreateRequestDto request, User user) {

        BinaryContent binaryContent = BinaryContent.createProfileImage(
                request.fileName(),
                request.contentType(),
                (long) request.bytes().length,
                user
        );
        binaryContentRepository.save(binaryContent);

        try {
            // OS가 지정한 기본 임시 디렉토리에 임시 파일 생성
            // 저장 예시) binary_123456789_id~~.tmp
            Path tempFile = Files.createTempFile(TEMP_FILE_PREFIX, "_" + binaryContent.getId() + TEMP_FILE_EXTENSION);

            Files.write(tempFile, request.bytes());
            eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), tempFile));
        } catch (IOException e) {
            log.error("임시 파일 생성 실패", e);
            throw new RuntimeException("임시 파일 저장 중 오류가 발생했습니다.");
        }

        user.setProfileImage(binaryContent);
    }

}