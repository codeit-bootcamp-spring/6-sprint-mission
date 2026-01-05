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
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.principal.DiscodeitUserDetails;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    // 유저 생성
    @Transactional
    public UserResponseDto create(UserCreateRequestDto request,
                                  BinaryContentCreateRequestDto profileImageRequest) {

        if (userRepository.existsByUsername(request.username())){
            throw UserAlreadyExistsException.byUsername(request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(user);

        if (profileImageRequest != null) {
            byte[] bytes = profileImageRequest.bytes();

            BinaryContent binaryContent = BinaryContent.builder()
                    .fileName(profileImageRequest.fileName())
                    .contentType(profileImageRequest.contentType())
                    .size((long) bytes.length)
                    .user(user)
                    .build();

            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), bytes);
            user.setProfileImage(binaryContent);
        }

        log.info("회원가입이 완료되었습니다. id=" + user.getId());
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        BinaryContentResponseDto profileImage = binaryContentMapper.toDto(user.getProfileImage());

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll(){
        List<User> users = userRepository.findAllWithStatusAndProfile(); // N+1 문제 해결 위해 fetch join 쿼리 사용

        return users.stream()
                .map(user -> {
                    BinaryContentResponseDto profileImage = binaryContentMapper.toDto(user.getProfileImage());

                    return userMapper.toDto(user);
                })
                .toList();
    }

    // 수정
    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
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
            byte[] bytes = profileImageRequest.bytes();
            BinaryContent binaryContent = BinaryContent.builder()
                    .fileName(profileImageRequest.fileName())
                    .contentType(profileImageRequest.contentType())
                    .size((long) profileImageRequest.bytes().length)
                    .build();
            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), bytes);
            user.setProfileImage(binaryContent);
        }


        userRepository.save(user); // 명시적 저장
        log.info("사용자 수정이 완료되었습니다. id=" + user.getId());

        BinaryContentResponseDto profileImage = binaryContentMapper.toDto(user.getProfileImage());
        return userMapper.toDto(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto updateUserRole(UserRoleUpdateRequest request) {

        UUID userId = request.userId();
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        user.updateRole(request.newRole());
        userRepository.save(user);

        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        for (Object principal : allPrincipals) {
            if (principal instanceof DiscodeitUserDetails userDetails) {
                if (userDetails.getUserId().equals(userId)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation session : sessions) {
                        session.expireNow();
                    }
                }
            }
        }

        return userMapper.toDto(user);
    }

    // 유저 삭제
    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);
        log.info("사용자 삭제가 완료되었습니다. id=" + userId);
    }

}