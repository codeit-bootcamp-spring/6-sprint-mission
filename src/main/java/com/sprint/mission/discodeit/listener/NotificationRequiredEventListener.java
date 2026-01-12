package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.UpdateRoleNotificationRequest;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {

    log.debug("메시지 알림 생성 시작: messageId={}", event.messageId());

    CreateMessageNotificationRequest request = CreateMessageNotificationRequest.builder()
        .channelId(event.channelId())
        .messageId(event.messageId())
        .authorId(event.authorId())
        .content(event.content())
        .build();

    notificationService.createMessageNotification(request);
  }

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {

    log.debug("역할 업데이트 알림 생성 시작: userId={}", event.userId());

    UpdateRoleNotificationRequest request = UpdateRoleNotificationRequest.builder()
        .userId(event.userId())
        .oldRole(event.oldRole())
        .newRole(event.newRole())
        .build();

    notificationService.createRoleUpdateNotification(request);
  }
}
