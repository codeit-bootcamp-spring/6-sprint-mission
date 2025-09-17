package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatus create(UserStatusCreateDto dto);

    UserStatus find(UUID id);

    List<UserStatus> findAll();

    UserStatus update(UUID id, UserStatusUpdateDto dto);

    UserStatus updateByUserId(UUID userId, UserStatusUpdateDto dto);

    boolean isOnlineByUserId(UUID userId, long minutesToConsiderOnline);

    void delete(UUID id);
}