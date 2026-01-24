package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.event.BinaryContentPutFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.UserRoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${discodeit.storage.type}")
    String storageType;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void onMessageCreated(MessageCreatedEvent event) {
        List<ReadStatus> readStatuses = readStatusRepository
                .findAllByChannelIdAndNotificationEnabledIsTrueAndUser_IdNot(event.channelDto().id(), event.userDto().id());

        readStatuses.forEach(readStatus -> {
            Notification notification = Notification.create(
                    readStatus.getUser(),
                    event.userDto().username() + " (" + event.channelDto().name() + ")",
                    event.messageDto().content()
            );
            notificationRepository.save(notification);
        });
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void onUserRoleUpdate(UserRoleUpdatedEvent event) {

        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new UserNotFoundException(event.userId()));

        final String roleUpdateMessage = "권한이 변경되었습니다.";
        Notification notification = Notification.create(
                user,
                roleUpdateMessage,
                event.oldRole().toString() + " -> "  + event.newRole().toString()
        );
        notificationRepository.save(notification);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void onBinaryContentPutFailure(BinaryContentPutFailEvent event) {

        final String title = storageType.equals("s3") ? "S3 파일 업로드 실패" : "파일 저장 실패";
        List<User> adminUsers = userRepository.findAllByRole(Role.ADMIN);
        adminUsers.forEach(user -> {
            Notification notification = Notification.createBinaryContentPutFailureNotification(
                    user,
                    title,
                    UUID.fromString(event.requestId()),
                    UUID.fromString(event.binaryContentId().toString()),
                    event.errorMessage()
            );
            notificationRepository.save(notification);
        });
    }

}
