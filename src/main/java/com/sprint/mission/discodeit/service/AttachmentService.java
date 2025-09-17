package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    void saveAttachments(UUID messageId, List<BinaryContentDto> attachments);
}