package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.event.StoragePutFailedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import java.util.UUID;
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

  public void putSuccess(UUID binaryContentId, byte[] file) {

    binaryContentStorage.put(binaryContentId, file);
    storageTxHandler.updateStorageStatus(binaryContentId, true);
  }

  public void putFailAndPublishEvent(Exception e, UUID binaryContentId) {

    if (binaryContentStorage instanceof S3BinaryContentStorage) {
      eventPublisher.publishEvent(StoragePutFailedEvent.builder()
          .requestId(MDC.get("requestId"))
          .binaryContentId(binaryContentId)
          .errorType(e.getClass().getSimpleName())
          .errorMessage(e.getMessage())
          .build());
    }

    storageTxHandler.updateStorageStatus(binaryContentId, false);
  }
}
