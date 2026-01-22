package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.event.FileUploadFailedEvent;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {

    String payload = null;
    try {

      payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
      log.info("Produced MessageCreatedEvent to Kafka: {}", payload);

    } catch (Exception e) {
      log.error("Failed to serialize MessageCreatedEvent: {}", e.getMessage());
    }

  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRoleUpdatedEvent(RoleUpdatedEvent event) {

    String payload = null;
    try {

      payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Produced RoleUpdatedEvent to Kafka: {}", payload);

    } catch (Exception e) {
      log.error("Failed to serialize RoleUpdatedEvent: {}", e.getMessage());
      return;
    }

  }

  @Async("eventTaskExecutor")
  @EventListener
  public void handleFileUploadFailedEvent(FileUploadFailedEvent event) {

    String payload = null;
    try {

      payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.FileUploadFailedEvent", payload);
      log.info("Produced FileUploadFailedEvent to Kafka: {}", payload);

    } catch (Exception e) {
      log.error("Failed to serialize FileUploadFailedEvent: {}", e.getMessage());
      return;
    }

  }

}
