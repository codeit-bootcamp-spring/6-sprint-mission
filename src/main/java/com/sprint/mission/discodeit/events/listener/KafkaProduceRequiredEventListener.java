package com.sprint.mission.discodeit.events.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.events.UploadFailedEvent;
import com.sprint.mission.discodeit.events.UserLogInOutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @EventListener
    public void on(UserLogInOutEvent event) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.UserLogInOutEvent", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @EventListener
    public void on(UploadFailedEvent event) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.UploadFailedEvent", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
