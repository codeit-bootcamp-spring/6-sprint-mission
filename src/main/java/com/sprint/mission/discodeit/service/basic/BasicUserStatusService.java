package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatus.CreateUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UpdateUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusDuplicateException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;
    private final UserRepository userRepository;

    @Override
    public UserStatusDto create(CreateUserStatusDto createUserStatusDTO) {
        log.info("유저 상태 생성 요청 수신: userId={}",createUserStatusDTO.userId());
        User user = userRepository.findById(createUserStatusDTO.userId())
                .orElseThrow(UserNotFoundException::new);

        if (userStatusRepository.findByUserId(createUserStatusDTO.userId()).isPresent()) {
            log.warn("사용자 상태 중복 발생: userId={} userStatusId={}",user.getId(),user.getStatus().getId());
            throw new UserStatusDuplicateException();
        }
        UserStatus userStatus = new UserStatus(user);
        log.info("유저 상태 생성 요청 수신: userStatus={}", userStatus.getId());
        return userStatusMapper.toDto(userStatusRepository.save(userStatus));
    }

    @Override
    public List<UserStatusDto> findAll() {
        log.info("유저 상태 목록 조회 요청 수신");
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public void update(UpdateUserStatusDto updateUserStatusDTO) {
        log.info("유저 상태 수정 요청 수신: userStatusId={}",updateUserStatusDTO.id());
        UserStatus userStatus = userStatusRepository.findById(updateUserStatusDTO.userId())
                .orElseThrow(UserNotFoundException::new);
        userStatus.update(updateUserStatusDTO.lastActiveAt());
        userStatusRepository.save(userStatus);
        log.info("유저 상태 수정 완료:  userStatus={}", userStatus.getId());
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest) {
        log.info("유저 접속 상태 수정 요청 수신: userId={}",userId);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        userStatus.isConnecting(userStatusUpdateRequest.newLastActiveAt());
        userStatusRepository.save(userStatus);
        log.info("유저 접속 상태 수정 완료: userId={}",userId);
        return userStatusMapper.toDto(userStatusRepository.save(userStatus));
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
