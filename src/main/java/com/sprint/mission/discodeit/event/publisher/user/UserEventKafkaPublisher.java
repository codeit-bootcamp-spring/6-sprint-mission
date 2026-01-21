package com.sprint.mission.discodeit.event.publisher.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.topic.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventKafkaPublisher {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(RoleUpdatedEvent event) {
        try {
            String kafkaEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(Topics.ROLE_UPDATED, kafkaEvent);
        } catch (JsonProcessingException e) {
            log.error("publish : RoleUpdatedEvent - Json Processing Error");
        }
    }
}
