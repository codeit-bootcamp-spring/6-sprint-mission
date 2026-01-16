package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {

        try {
            byte[] bytes =Files.readAllBytes(event.tempFilePath());
            binaryContentStorage.put(event.id(), bytes);
            binaryContentService.updateStatus(event.id(), BinaryContent.BinaryContentStatus.SUCCESS);

        } catch (Exception e) {
            log.warn("Binary content storage failed for ID: {}", event.id(), e);
            binaryContentService.updateStatus(event.id(), BinaryContent.BinaryContentStatus.FAIL);

        } finally { // 임시 파일 삭제
            try {
                Files.deleteIfExists(event.tempFilePath());
            } catch (IOException e) {
                log.error("임시 파일 삭제 실패: {}", event.tempFilePath());
            }
        }
    }
}