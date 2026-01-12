package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(BinaryContentCreatedEvent event) {
    binaryContentRepository.findById(event.getBinaryContentId())
        .ifPresentOrElse(binaryContent -> {
          try {
            if (binaryContentStorage.put(event.getBinaryContentId(), event.getBytes()) != null) {
              binaryContent.updateStatus(BinaryContentStatus.SUCCESS);
            } else {
              binaryContent.updateStatus(BinaryContentStatus.FAIL);
              log.warn("Binary content upload failed after retries: id={}",
                  event.getBinaryContentId());
            }
          } catch (Exception ex) {
            log.warn("Binary content upload failed: id={}", event.getBinaryContentId(), ex);
            binaryContent.updateStatus(BinaryContentStatus.FAIL);
          }
        }, () -> log.warn("Binary content not found for upload: id={}",
            event.getBinaryContentId()));
  }
}
