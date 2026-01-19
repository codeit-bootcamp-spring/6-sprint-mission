package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.event.NotificationEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotificationCreatedEvent(NotificationEvent event) {

    sseService.send(
        List.of(event.getReceiverId()),
        "notifications.created",
        event.getData()
    );

  }

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentUpdateEvent(BinaryContentUpdatedEvent event) {

    sseService.send(
        List.of(event.getReceiverId()),
        "binaryContents.updated",
        event.getData()
    );

  }

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleChannelUpdateEvent(ChannelUpdatedEvent event) {

    sseService.send(
      List.of(event.getReceiverId()),
      event.getType().getEventName(),
      event.getData()
    );

  }

}
