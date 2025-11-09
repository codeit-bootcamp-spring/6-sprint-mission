package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentSave;
import com.sprint.mission.discodeit.dto.User.*;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.file.FileInPutException;
import com.sprint.mission.discodeit.exception.file.FileOutPutException;
import com.sprint.mission.discodeit.exception.user.UserDuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.UserDuplicateNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public UserDto create(MultipartFile multipartFile, UserCreateRequest userCreateRequest) {

        log.info("유저 생성 요청 수신: userName={} userEmail={}",userCreateRequest.username(), userCreateRequest.email());

        //유저 중복 확인
        isDuplicate(userCreateRequest.username(), userCreateRequest.email());

        //파일 있으면 파일 생성
        BinaryContentSave profileBinaryContent = getBinaryContent(multipartFile);
        BinaryContent profile = null;
        if (profileBinaryContent != null) {
            profile = profileBinaryContent.binaryContent();
            binaryContentStorage.put(profile.getId(), profileBinaryContent.data());
        }

        //유저 생성
        User user = new User(
                userCreateRequest.username(),
                userCreateRequest.email(),
                userCreateRequest.password(),
                profile
        );

        //유저에 유저 상태 추가
        UserStatus userStatus = new UserStatus(user);
        user.update(UpdateUserDto.getStatus(userStatus));

        if (profile != null) {
            binaryContentRepository.save(profile);
        }
        userRepository.save(user);
        userStatusRepository.save(userStatus);
        log.info("유저 생성 완료: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("유저 목록 조회 요청 수신");
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    @Override
    public UserDto update(MultipartFile multipartFile, UUID userId, UserUpdateRequest userUpdateRequest) {
        log.info("사용자 수정 요청 수신: userId={}", userId);
        //유저 중복 확인
        isDuplicate(userUpdateRequest.newUsername(), userUpdateRequest.newEmail());

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        //파일 있으면 파일 생성
        BinaryContentSave profileBinaryContent = getBinaryContent(multipartFile);

        BinaryContent profile = null;
        if (profileBinaryContent != null) {
            profile = profileBinaryContent.binaryContent();
            binaryContentStorage.put(profile.getId(), profileBinaryContent.data());
        }

        user.update(UpdateUserDto.getUpdateUser(
                user.getId(),
                userUpdateRequest,
                profile
        ));

        if (profile != null) {
            binaryContentRepository.save(profile);
        }
        userRepository.save(user);
        log.info("사용자 수정 완료: userId={}", userId);

        return userMapper.toDto(user);
}

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(UserStatusNotFoundException::new);

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        binaryContentRepository.deleteById(user.getProfile().getId());
        userStatusRepository.deleteById(userStatus.getId());
        userRepository.deleteById(id);
    }

    public void isDuplicate(String name, String email) {
        log.info("이메일, 이름 중복 확인중");
        if(userRepository.existsByEmail(email)) {
            log.error("이메일 중복 발생: email={}", email);
            throw new UserDuplicateEmailException();
        }
        if (userRepository.existsByUsername(name)) {
            log.error("이름 중복 발생: username={}", name);
            throw new UserDuplicateNameException();
        }
        log.info("이메일, 이름 중복 확인 완료");
    }

    public BinaryContentSave getBinaryContent(MultipartFile multipartFile) {
        log.info("프로필 파일 저장 시작");
        if (multipartFile.isEmpty()) {
            log.info("메시지 첨부파일 없음");
            return null;
        } else {
            try {
                BinaryContent binaryContent = new BinaryContent(
                        multipartFile.getOriginalFilename(),
                        multipartFile.getSize(),
                        multipartFile.getContentType()
                );
                log.info("메시지 첨부파일 저장 완료");
                return new BinaryContentSave(binaryContent, multipartFile.getBytes());
            } catch (IOException e) {
                log.error("사용자 프로필 파일 저장 오류 발생: 파일 이름={}",multipartFile.getOriginalFilename());
                throw new FileOutPutException();
            }
        }
    }
}
