package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.Message.MessageCreatedEvent;
import com.sprint.mission.discodeit.dto.User.RoleUpdatedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {


    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;


    private final String ROLE_UPDATE_TITLE = "권한이 변경되었습니다.";

    @KafkaListener(topics ="discodeit.MessageCreatedEvent" )
    public void onMessageCreatedEvent(String kafkaEvent){
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            String title = event.userName()+" (#"+event.channelName()+")";
            notificationService.create(event.userId(),title,event.content());
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,RoleUpdatedEvent.class);
            String content = event.oldRole() +" -> "+event.newRole();
            notificationService.create(event.userId(),ROLE_UPDATE_TITLE,content);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
