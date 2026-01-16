package com.sprint.mission.discodeit.event.listener.kafka;

import com.sprint.mission.discodeit.dto.kafka.BinaryContentPutFailPayload;
import com.sprint.mission.discodeit.dto.kafka.MessageCreatedPayload;
import com.sprint.mission.discodeit.dto.kafka.UserRoleUpdatedPayload;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.BinaryContentPutFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.UserRoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        MessageCreatedPayload messageCreatedPayload = new MessageCreatedPayload(

        );
        String payload = objectMapper.writeValueAsString(messageCreatedPayload);
        kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserRoleUpdatedEvent event) {
        UserRoleUpdatedPayload userRoleUpdatedPayload = new UserRoleUpdatedPayload(

        );
        String payload = objectMapper.writeValueAsString(userRoleUpdatedPayload);
        kafkaTemplate.send("discodeit.UserRoleUpdatedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(BinaryContentPutFailEvent event) {
        BinaryContentPutFailPayload binaryContentPutFailPayload = new BinaryContentPutFailPayload(

        );
        String payload = objectMapper.writeValueAsString(binaryContentPutFailPayload);
        kafkaTemplate.send("discodeit.BinaryContentPutFailEvent", payload);
    }
}


