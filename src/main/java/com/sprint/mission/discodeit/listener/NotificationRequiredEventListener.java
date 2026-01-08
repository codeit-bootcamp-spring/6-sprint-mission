package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.UserRoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        List<ReadStatus> readStatuses = readStatusRepository
                .findAllByChannelIdAndNotificationEnabledIsTrueAndUser_IdNot(event.channel().getId(), event.author().getId());

        readStatuses.forEach(readStatus -> {
            Notification notification = Notification.create(
                    readStatus.getUser(),
                    event.author().getUsername() + " (" + event.channel().getName() + ")",
                    event.content()
            );
            notificationRepository.save(notification);
        });
    }

    @TransactionalEventListener
    public void on(UserRoleUpdatedEvent event) {

        final String roleUpdateMessage = "권한이 변경되었습니다.";
        Notification notification = Notification.create(
                event.user(),
                roleUpdateMessage,
                event.oldRole().toString() + " -> "  + event.newRole().toString()
        );
        notificationRepository.save(notification);
    }

}
