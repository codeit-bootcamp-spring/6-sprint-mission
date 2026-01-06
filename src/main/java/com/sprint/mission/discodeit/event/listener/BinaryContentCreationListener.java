package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentCreationListener {

  private final BinaryContentStorage binaryContentStorage;

  @Async
  @EventListener
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {

    binaryContentStorage.put(event.getId(), event.getBinaryContent());

  }

}
