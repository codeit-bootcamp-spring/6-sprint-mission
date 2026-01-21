package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.CacheUpdater;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final CacheUpdater cacheUpdater;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) {
        String title = String.format("%s (#%s)", event.getAuthorName(), event.getChannelName());
        String content = event.getContent();

        List<Notification> notifications = readStatusRepository
            .findAllByChannelIdAndNotificationEnabledTrueWithUser(event.getChannelId())
            .stream()
            .filter(readStatus -> !readStatus.getUser().getId().equals(event.getAuthorId()))
            .map(readStatus -> new Notification(readStatus.getUser(), title, content))
            .toList();

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            log.info("메시지 알림 생성 완료: messageId={}, count={}",
                event.getMessageId(), notifications.size());
            notifications.stream()
                .map(notification -> notification.getReceiver().getId())
                .distinct()
                .forEach(this::refreshUserNotificationsCache);
        }
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        userRepository.findById(event.userId())
            .ifPresentOrElse(user -> {
                String title = "권한이 변경되었습니다.";
                String content = String.format("%s -> %s", event.previousRole(), event.newRole());
                notificationRepository.save(new Notification(user, title, content));
                refreshUserNotificationsCache(user.getId());
            }, () -> log.warn("권한 변경 알림 대상 사용자 없음: userId={}", event.userId()));
    }

    private void refreshUserNotificationsCache(UUID receiverId) {
        List<Notification> notifications = notificationRepository
            .findAllByReceiverIdOrderByCreatedAtDesc(receiverId);
        cacheUpdater.putUserNotifications(
            receiverId,
            notifications.stream().map(notificationMapper::toDto).toList()
        );
    }
}
