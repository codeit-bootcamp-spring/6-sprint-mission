package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(UUID userId, BinaryContentCreateRequest request);

    BinaryContentDto find(UUID binaryContentId);

    List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

    BinaryContentDto updateStatus(UUID userId, UUID binaryContentId, BinaryContentStatus status);

    void delete(UUID binaryContentId);
}
