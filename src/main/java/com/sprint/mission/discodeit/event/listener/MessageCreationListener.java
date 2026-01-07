package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageCreationListener {

  @Async
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {
    // No implementation yet
  }

}
