package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateRoleNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateStorageNotificationRequest;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.StoragePutFailedEvent;
import com.sprint.mission.discodeit.handler.NotificationTaskHandler;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "discodeit.event",
    name = "type",
    havingValue = "spring"
)
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationTaskHandler notificationTaskHandler;

  @Timed("async.notification.message.create")
  @Async("notificationEventTaskExecutor")
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 500, multiplier = 2.0)
  )
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {

    log.debug("메시지 알림 생성 시작: messageId={}", event.message().getId());

    CreateMessageNotificationRequest request = CreateMessageNotificationRequest.builder()
        .channelId(event.channelId())
        .channelName(event.channelName())
        .messageId(event.message().getId())
        .authorId(event.authorId())
        .authorName(event.authorName())
        .content(event.message().getContent())
        .build();

    notificationTaskHandler.createMessageNotificationTask(request);
  }

  @Async("notificationEventTaskExecutor")
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 500, multiplier = 2.0)
  )
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {

    log.debug("역할 업데이트 알림 생성 시작: userId={}", event.userId());

    CreateRoleNotificationRequest request = CreateRoleNotificationRequest.builder()
        .userId(event.userId())
        .oldRole(event.oldRole())
        .newRole(event.newRole())
        .build();

    notificationTaskHandler.createRoleUpdateNotificationTask(request);
  }

  @EventListener
  public void on(StoragePutFailedEvent event) {

    log.debug("스토리지 저장 실패 이벤트 수신: binaryContentId={}", event.binaryContentId());

    CreateStorageNotificationRequest request = CreateStorageNotificationRequest.builder()
        .binaryContentId(event.binaryContentId())
        .errorType(event.errorType())
        .errorMessage(event.errorMessage())
        .build();

    notificationTaskHandler.createStorageNotificationTask(request);
  }

  @Recover
  public void recoverMessage(Exception e, MessageCreatedEvent event) {

    log.error("메시지 알림 생성 실패: messageId={}", event.message().getId(), e);

    throw new RuntimeException("비동기 메시지 알림 최종 실패", e);
  }

  @Recover
  public void recoverRoleUpdate(Exception e, RoleUpdatedEvent event) {

    log.error("역할 업데이트 알림 생성 실패: userId={}", event.userId(), e);

    throw new RuntimeException("비동기 역할 업데이트 알림 최종 실패", e);
  }
}
