package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

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

        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .password(request.password())
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        user.setUserStatus(userStatus);
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

                    return userMapper.toDto(user/*, user.getUserStatus(), profileImage*/);
                })
                .toList();
    }

    // 수정
    @Transactional
    public UserResponseDto update(UUID id, UserUpdateRequestDto request,
                                  BinaryContentCreateRequestDto profileImageRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.newUsername() != null){
            userRepository.findByUsername(request.newUsername())
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .ifPresent(existingUser -> {
                        throw UserAlreadyExistsException.byUsername(request.newUsername());
                    });
            user.setUsername(request.newUsername());
        }

        if (request.newEmail() != null) {
            userRepository.findByEmail(request.newEmail())
                    .filter(existingUser -> !existingUser.getEmail().equals(user.getEmail()))
                    .ifPresent(existingUser -> {
                        throw new UserAlreadyExistsException(request.newEmail());
                    });
            user.setEmail(request.newEmail());
        }

        if (request.newPassword() != null) {
            user.setPassword(request.newPassword());
        }

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
        return userMapper.toDto(user/*, user.getUserStatus(), profileImage*/);
    }

    // 유저 삭제
    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
        log.info("사용자 삭제가 완료되었습니다. id=" + id);
    }

}