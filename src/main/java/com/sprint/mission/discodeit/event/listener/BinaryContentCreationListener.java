package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentCreationListener {

  private final BinaryContentStorage binaryContentStorage;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {

    binaryContentStorage.put(event.getId(), event.getBinaryContent());

  }

}
