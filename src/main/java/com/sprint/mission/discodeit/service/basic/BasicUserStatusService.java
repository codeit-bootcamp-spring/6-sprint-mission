package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.user.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public BasicUserStatusService(UserRepository userRepository, UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public UserStatus create(UserStatusCreateDto dto) {
        userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException(dto.userId() + " not found."));

        if (userStatusRepository.findByUserId(dto.userId()).isPresent()) {
            throw new IllegalStateException(dto.userId() + " already exists.");
        }

        UserStatus userStatus = new UserStatus(dto.userId(), dto.status());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id + " not found."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id + " not found."));

        if (dto.status() != null) {
            userStatus.setStatus(dto.status());
        }

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(userId + " not found."));

        if (dto.status() != null) {
            userStatus.setStatus(dto.status());
        }

        return userStatusRepository.save(userStatus);
    }

    @Override
    public boolean isOnlineByUserId(UUID userId, long minutesToConsiderOnline) {
        return userStatusRepository.isOnlineByUserId(userId, minutesToConsiderOnline);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}