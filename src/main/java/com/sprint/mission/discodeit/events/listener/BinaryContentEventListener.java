package com.sprint.mission.discodeit.events.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void binaryContentCreated(BinaryContentCreatedEvent event) {
        if (event == null
                || event.getBinaryContentId() == null
                || event.getBytes() == null
                || event.getBytes().length == 0)
            throw new BinaryContentException(ErrorCode.BINARY_CONTENT_NOT_FOUND);

        try {
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
            binaryContentService.updateStatus(event.getUserId(), event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
        } catch (RuntimeException e) {
            binaryContentService.updateStatus(event.getUserId(), event.getBinaryContentId(), BinaryContentStatus.FAIL);
        }
    }
}
