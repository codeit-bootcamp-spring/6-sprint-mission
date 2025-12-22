package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    public UserStatusResponseDto create(UserStatusCreateRequestDto dto){

        if (userStatusRepository.existsById(dto.userId())) {
            throw new UserStatusAlreadyExistsException(dto.userId());
        }

        User user = userRepository.findById(dto.userId()).orElseThrow(
                () -> new UserNotFoundException(dto.userId()));

        UserStatus userStatus = userStatusMapper.toEntity(dto);
        userStatus.updateLastActiveAt(Instant.now());

        userStatusRepository.save(userStatus);
        log.info("UserStatus 생성 완료: " +  userStatus.getId());

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional(readOnly = true)
    public UserStatusResponseDto findById(UUID id){
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new UserStatusNotFoundException(id));
        return userStatusMapper.toDto(userStatus);
    }

    @Transactional(readOnly = true)
    public List<UserStatusResponseDto> findAll(){
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        return userStatuses.stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserStatusResponseDto update(UUID id, UserStatusUpdateRequestDto dto){
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new UserStatusNotFoundException(id));
        userStatus.updateLastActiveAt(Instant.now());
        log.info("UserStatus 번경 완료: " + userStatus.getId());
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequestDto dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> UserStatusNotFoundException.byUserId(userId));

        userStatus.updateLastActiveAt(dto.newLastActiveAt());
        log.info("UserStatus 번경 완료: " + userStatus.getId());
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public void deleteById(UUID id){
        userStatusRepository.deleteById(id);
        log.info("UserStatus 삭제 완료: " + id);
    }

}
