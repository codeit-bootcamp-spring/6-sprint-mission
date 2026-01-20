package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.enumtype.BinaryContentStatus;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto findById(UUID id);
    List<BinaryContentDto> findAll();
    BinaryContentDto updateStatus(UUID binaryContentId ,BinaryContentStatus status);
}
