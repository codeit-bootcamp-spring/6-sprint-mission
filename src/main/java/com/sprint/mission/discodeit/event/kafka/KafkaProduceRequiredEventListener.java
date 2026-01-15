package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.Message.MessageCreatedEvent;
import com.sprint.mission.discodeit.dto.User.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.MessageCreatedEvent",payload);
    }

    @Async
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.RoleUpdatedEvent",payload);
    }
}
