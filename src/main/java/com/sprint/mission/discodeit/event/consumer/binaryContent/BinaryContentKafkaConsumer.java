package com.sprint.mission.discodeit.event.consumer.binaryContent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.topic.Topics;
import com.sprint.mission.discodeit.service.event.EventBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final EventBinaryContentService eventBinaryContentService;

    @KafkaListener(topics = Topics.BINARY_CONTENT_CREATED)
    public void onBinaryContentCreatedEvent(String kafkaEvent) {
        try {
            BinaryContentCreatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentCreatedEvent.class);

            BinaryContentStorage storage = event.getBinaryContentStorage();
            UUID binaryContentId = event.getBinaryContentId();
            byte[] bytes = event.getBytes();

            storage.put(binaryContentId, bytes);
            eventBinaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
            log.info("[{}] BinaryContent create success", Thread.currentThread().getName());
        } catch (JsonProcessingException e) {
            log.error("consumer : BinaryContentCreatedEvent - Json Processing Error");
        }
    }
}
