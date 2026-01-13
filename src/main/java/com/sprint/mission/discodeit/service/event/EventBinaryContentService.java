package com.sprint.mission.discodeit.service.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class EventBinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public void updateStatus(UUID binaryContentId, BinaryContentStatus binaryContentStatus) {
        BinaryContent foundContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

        foundContent.updateStatus(binaryContentStatus);
    }
}
