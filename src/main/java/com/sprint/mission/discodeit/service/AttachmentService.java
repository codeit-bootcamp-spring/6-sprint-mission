package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.BinaryContentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentService {
    void saveAttachments(UUID messageId, List<BinaryContentDto> attachments);

    List<BinaryContentDto> findAllByMessageId(UUID messageId);

    Optional<BinaryContentDto> findByMessageIdAndFilename(UUID messageId, String filename);
}
