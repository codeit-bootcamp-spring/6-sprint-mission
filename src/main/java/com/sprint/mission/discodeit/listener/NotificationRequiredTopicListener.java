package com.sprint.mission.discodeit.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.StoragePutFailedEvent;
import com.sprint.mission.discodeit.handler.NotificationTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final NotificationTaskHandler notificationTaskHandler;
  private final ObjectMapper objectMapper;

  @RetryableTopic(
      include = {Exception.class},
      attempts = "3",
      backoff = @Backoff(delay = 500, multiplier = 2.0),
      dltStrategy = DltStrategy.FAIL_ON_ERROR,
      // ex. 토픽 이름: discodeit.MessageCreatedEvent-retry-0, ..., discodeit.MessageCreatedEvent-dlt
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
  )
  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      log.debug("메시지 알림 생성 시작: messageId={}", event.messageId());

      notificationTaskHandler.createMessageNotificationTask(event);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RetryableTopic(
      include = {Exception.class},
      attempts = "3",
      backoff = @Backoff(delay = 500, multiplier = 2.0),
      dltStrategy = DltStrategy.FAIL_ON_ERROR,
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
  )
  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {

    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      log.debug("역할 업데이트 알림 생성 시작: userId={}", event.userId());

      notificationTaskHandler.createRoleUpdateNotificationTask(event);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RetryableTopic(
      include = {Exception.class},
      attempts = "3",
      backoff = @Backoff(delay = 500, multiplier = 2.0),
      dltStrategy = DltStrategy.FAIL_ON_ERROR,
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
  )
  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onStorageUploadFailedEvent(String kafkaEvent) {

    try {
      StoragePutFailedEvent event = objectMapper.readValue(kafkaEvent, StoragePutFailedEvent.class);

      log.debug("스토리지 저장 실패 이벤트 수신: binaryContentId={}", event.binaryContentId());

      notificationTaskHandler.createStorageNotificationTask(event);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @DltHandler
  public void handleDlt(String kafkaEvent,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      Exception e) {

    log.error("최종 실패 DLT 핸들러 호출: topic={}, event={}", topic, kafkaEvent, e);
  }
}
