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

    // 로컬, s3 저장 둘 다 실패 이벤트 발행
    eventPublisher.publishEvent(StoragePutFailedEvent.builder()
        .requestId(MDC.get("requestId"))
        .binaryContentId(binaryContentId)
        .errorType(e.getClass().getSimpleName())
        .errorMessage(e.getMessage())
        .build());

    storageTxHandler.updateStorageStatus(binaryContentId, false);
  }
}
