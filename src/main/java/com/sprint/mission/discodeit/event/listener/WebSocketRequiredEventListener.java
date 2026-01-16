package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate simpMessagingTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {
    simpMessagingTemplate.convertAndSend(
        "/sub/channels/" + event.getChannelId() + "/messages",
        event
    );
  }

}
