package com.sprint.mission.discodeit.event.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.kafka.BinaryContentPutFailPayload;
import com.sprint.mission.discodeit.dto.kafka.MessageCreatedPayload;
import com.sprint.mission.discodeit.dto.kafka.UserRoleUpdatedPayload;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.event.BinaryContentPutFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    @Value("${discodeit.storage.type}")
    private String storageType;
    private final String PUT_FAILURE_TITLE = storageType.equals("s3") ? "S3 파일 업로드 실패" : "파일 저장 실패";

    private static final String ROLE_UPDATE_MESSAGE = "권한이 변경되었습니다.";

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreated(String kafkaEvent) {
        try {
            MessageCreatedPayload payload
                    = objectMapper.readValue(kafkaEvent, MessageCreatedPayload.class);
            List<ReadStatus> readStatuses = readStatusRepository
                    .findAllByChannelIdAndNotificationEnabledIsTrueAndUser_IdNot(payload.channelId(), payload.authorId());

            readStatuses.forEach(readStatus -> {
                Notification notification = Notification.create(
                        readStatus.getUser(),
                        payload.authorUsername() + " (" + payload.channelName() + ")",
                        payload.content()
                );
                notificationRepository.save(notification);
            });
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onUserRoleUpdate(String kafkaEvent) {
        try {
            UserRoleUpdatedPayload payload
                    = objectMapper.readValue(kafkaEvent, UserRoleUpdatedPayload.class);
            User user = userRepository.findById(payload.userId())
                    .orElseThrow(() -> new UserNotFoundException(payload.userId()));
            Notification notification = Notification.create(
                    user,
                    ROLE_UPDATE_MESSAGE,
                    payload.oldRole().toString() + " -> "  + payload.newRole().toString()
            );
            notificationRepository.save(notification);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onBinaryContentPutFailure(String kafkaEvent) {
        try {
            BinaryContentPutFailPayload payload
                    = objectMapper.readValue(kafkaEvent, BinaryContentPutFailPayload.class);

            List<User> adminUsers = userRepository.findAllByRole(Role.ADMIN);
            adminUsers.forEach(user -> {
                Notification notification = Notification.create(
                        user,
                        PUT_FAILURE_TITLE,
                        "requestId: " + payload.requestId() + '\n' +
                                "binaryContentId: " + payload.binaryContentId() + '\n' +
                                "error: " + payload.errorMessage()
                );
                notificationRepository.save(notification);
            });
        } catch (JsonProcessingException e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}