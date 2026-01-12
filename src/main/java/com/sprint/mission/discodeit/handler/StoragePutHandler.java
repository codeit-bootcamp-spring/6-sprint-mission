package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.StoragePutFailedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoragePutHandler {

  private final BinaryContentStorage binaryContentStorage;
  private final StorageTxHandler storageTxHandler;
  private final ApplicationEventPublisher eventPublisher;

  public void putSuccess(BinaryContentCreatedEvent event) {

    binaryContentStorage.put(event.binaryContentId(), event.file());
    storageTxHandler.updateStorageStatus(event.binaryContentId(), true);
  }

  public void putFailAndPublishEvent(Exception e, BinaryContentCreatedEvent event) {

    if (binaryContentStorage instanceof S3BinaryContentStorage) {
      eventPublisher.publishEvent(StoragePutFailedEvent.builder()
          .requestId(MDC.get("requestId"))
          .binaryContentId(event.binaryContentId())
          .errorType(e.getClass().getSimpleName())
          .errorMessage(e.getMessage())
          .build());
    }

    storageTxHandler.updateStorageStatus(event.binaryContentId(), false);
  }
}
