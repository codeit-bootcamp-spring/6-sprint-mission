package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {

  }

  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {

  }

}
