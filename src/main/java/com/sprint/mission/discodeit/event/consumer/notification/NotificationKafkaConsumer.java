package com.sprint.mission.discodeit.event.consumer.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.event.FileUploadFailedEvent;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.NotificationContentFormatter;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.topic.Topics;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.event.EventNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final EventNotificationService eventNotificationService;

    //kafka 스레드로 비동기
    //메서드 별 스레드 수 지정 가능(미설정 시 기본값 = 1(yml 파일 전역 설정))
    //유휴 시 종료와 같은 세부 설정은 없고 지정된 스레드 수만큼 항상 고정
    @KafkaListener(topics = Topics.MESSAGE_CREATED)
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                    MessageCreatedEvent.class);

            String content = String.format(
                    NotificationContentFormatter.CREATED_MESSAGE.getFormatter(),
                    event.getSenderUsername(),
                    event.getContent()
            );

            eventNotificationService.notifyCreatedMessage(
                    event.getSenderUsername(),
                    content,
                    event.getChannelId()
            );

        } catch (JsonProcessingException e) {
            log.error("consumer : MessageCreatedEvent - Json Processing Error");
        } catch (Exception e) {
            log.error("consumer : MessageCreatedEvent - Notification Created Failed");
        }
    }

    @KafkaListener(topics = Topics.ROLE_UPDATED)
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            String content = String.format(
                    NotificationContentFormatter.UPDATED_ROLE.getFormatter(),
                    event.getBefore().name(),
                    event.getAfter().name()
            );

            eventNotificationService.notifyUpdatedRole(event.getReceiverId(), content);
            log.info("MessageCreatedEvent - Notification Create Success");
        } catch (JsonProcessingException e) {
            log.error("consumer : RoleUpdatedEvent - Json Processing Error");
        } catch (UserNotFoundException e) {
            log.error("consumer : RoleUpdatedEvent - Receiver Not Found");
        } catch (Exception e) {
            log.error("consumer : RoleUpdatedEvent - Notification Created Failed");
        }
    }

    @KafkaListener(topics = Topics.FILE_UPLOAD_FAILED)
    public void onFileUploadFailedEvent(String kafkaEvent) {
        try {
            FileUploadFailedEvent event = objectMapper.readValue(kafkaEvent, FileUploadFailedEvent.class);
            String content = String.format(
                    NotificationContentFormatter.FILE_UPLOAD_FAILED.getFormatter(),
                    "RequestId", event.getRequestId(),
                    "BinaryContentId", event.getBinaryContentId(),
                    "Error", event.getError()
            );

            eventNotificationService.notifyAdminOfError(content);
            log.info("FileUploadFailedEvent - Notification Create Success");
        } catch (JsonProcessingException e) {
            log.error("consumer : FileUploadFailedEvent - Json Processing Error");
        } catch (Exception e) {
            log.error("consumer : FileUploadFailedEvent - Notification Created Failed");
        }
    }
}
