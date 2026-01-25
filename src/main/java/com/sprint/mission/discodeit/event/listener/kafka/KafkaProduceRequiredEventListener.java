package com.sprint.mission.discodeit.event.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.kafka.BinaryContentPutFailPayload;
import com.sprint.mission.discodeit.dto.kafka.MessageCreatedPayload;
import com.sprint.mission.discodeit.dto.kafka.UserRoleUpdatedPayload;
import com.sprint.mission.discodeit.event.BinaryContentPutFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.UserRoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) throws JsonProcessingException {
        MessageCreatedPayload messageCreatedPayload = new MessageCreatedPayload(
                event.channelDto().id(),
                event.channelDto().name(),
                event.userDto().id(),
                event.userDto().username(),
                event.messageDto().content()
        );
        String payload = objectMapper.writeValueAsString(messageCreatedPayload);
        kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserRoleUpdatedEvent event) throws JsonProcessingException{
        UserRoleUpdatedPayload userRoleUpdatedPayload = new UserRoleUpdatedPayload(
                event.userId(),
                event.oldRole(),
                event.newRole()
        );
        String payload = objectMapper.writeValueAsString(userRoleUpdatedPayload);
        kafkaTemplate.send("discodeit.UserRoleUpdatedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(BinaryContentPutFailEvent event) throws JsonProcessingException{
        BinaryContentPutFailPayload binaryContentPutFailPayload = new BinaryContentPutFailPayload(
                event.requestId(),
                event.binaryContentId(),
                event.errorMessage()
        );
        String payload = objectMapper.writeValueAsString(binaryContentPutFailPayload);
        kafkaTemplate.send("discodeit.BinaryContentPutFailEvent", payload);
    }
}


