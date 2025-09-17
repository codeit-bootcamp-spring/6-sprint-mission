package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.MessageDto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(ReadStatusCreateDto dto);

    ReadStatus find(UUID id);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(UUID id, ReadStatusUpdateDto dto);

    void delete(UUID userId,UUID channelId);
}
