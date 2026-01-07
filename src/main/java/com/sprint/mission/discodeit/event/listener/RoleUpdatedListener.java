package com.sprint.mission.discodeit.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleUpdatedListener {

  @Async
  public void handleRoleUpdatedEvent() {
    // No implementation yet
  }

}
