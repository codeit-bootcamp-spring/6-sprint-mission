package com.sprint.mission.discodeit.event.publisher.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
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
public class MessageEventKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(MessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(Topics.MESSAGE_CREATED, payload); //send는 비동기
        } catch (JsonProcessingException e) {
            log.error("publish : MessageCreatedEvent - Json Processing Error");
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(BinaryContentCreatedEvent event) {
        try {
            String kafkaEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(Topics.BINARY_CONTENT_CREATED, kafkaEvent);
        } catch (JsonProcessingException e) {
            log.error("publish : BinaryContentCreatedEvent - Json Processing Error");
        }
    }
}