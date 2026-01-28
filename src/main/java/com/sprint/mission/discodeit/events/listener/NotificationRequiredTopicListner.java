package com.sprint.mission.discodeit.events.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.events.UploadFailedEvent;
import com.sprint.mission.discodeit.events.UserLogInOutEvent;
import com.sprint.mission.discodeit.jwt.JwtRegistry;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListner {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final ReadStatusService readStatusService;
    private final UserService userService;
    private final SseService sseService;
    private final NotificationMapper notificationMapper;

    @Async
    @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            if (event == null)
                return;

            //public 채널 생성시에는 read status가 없고, 요구사항이 명확하지 않아 임의로 구현
            //public 채널에는 모든 유저가 포함되어 있다고 간주.
            //public 채널 메세지의 경우, 모든 유저 중, read-status 가 있는 유저는 알림여부를 확인하고
            //없는 유저는 public 채널의 알림여부 기본값이 false 이므로 보내지 않음.
            //즉, read-status가 있는 유저 중, 채널 타입에 관계 없이 알림여부에 따라 발송 하면됨

            List<Notification> notificationList = readStatusService.findAllByChannelId(event.getChannel().getId())
                    .stream()
                    .filter(x -> x.userId() != event.getAuthorId())
                    .map(x -> Notification.builder()
                            .title(event.getTitle())
                            .content(event.getContent())
                            .receiverId(x.userId())
                            .build()).toList();

            notificationService.saveAllNotifications(notificationList);

            notificationList.forEach(notification -> {
                sendSSe(notification);
            });

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Async
    @KafkaListener(topics = "discodeit.UserLogInOutEvent", groupId = "discodeit-group")
    public void onUserLogInOutEvent(String kafkaEvent) {
        try {
            UserLogInOutEvent event = objectMapper.readValue(kafkaEvent, UserLogInOutEvent.class);
            if (event == null)
                return;

            sseService.broadcast("users.updated", event);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
    public void onRoleUpdated(String kafkaEvent) {

        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            if (event == null)
                return;

            Notification notification = Notification.builder()
                    .title(event.getTitle())
                    .content(event.getContent())
                    .receiverId(event.getReceiverId()).build();

            notificationService.saveNotification(notification);
            sendSSe(notification);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @KafkaListener(topics = "discodeit.UploadFailedEvent", groupId = "discodeit-group")
    public void onUploadFailedEvent(String kafkaEvent) {

        try {
            UploadFailedEvent event = objectMapper.readValue(kafkaEvent, UploadFailedEvent.class);

            if (event == null)
                return;

            List<UserDto> admin = userService.findAllByRole(Role.ROLE_ADMIN);
            if (admin == null || admin.size() == 0) {
                return;
            }

            List<Notification> notifications = admin.stream().map(x -> Notification.builder()
                    .title(event.getTitle())
                    .content(event.getContent())
                    .receiverId(x.id()).build()).toList();

            notificationService.saveAllNotifications(notifications);
            notifications.forEach(notification -> {
                sendSSe(notification);
            });
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSSe(Notification notification) {
        NotificationDto notificationDto = notificationMapper.toDto(notification);
        sseService.send(List.of(notificationDto.receiverId()), "notifications.created", notificationDto);
    }
}
