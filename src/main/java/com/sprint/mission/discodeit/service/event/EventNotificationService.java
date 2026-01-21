package com.sprint.mission.discodeit.service.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.event.NotificationTitle;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventNotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    @Value("${discodeit.admin.username}")
    private String adminName;

    public void notifyCreatedMessage(String senderUsername, String content, UUID channelId) {
        List<ReadStatus> targetReadStatuses = getNotificationEnabledReadStatuses(channelId);
        targetReadStatuses.removeIf(readStatus -> readStatus.getUser().getUsername().equals(senderUsername));

        List<Notification> notifications = new ArrayList<>();

        for (ReadStatus readStatus : targetReadStatuses) {
            Notification notification = new Notification(
                    readStatus.getUser(),
                    NotificationTitle.CREATED_MESSAGE.getTitle(),
                    content
            );
            notifications.add(notification);
        }
        notificationRepository.saveAll(notifications);
    }

    public void notifyUpdatedRole(UUID receiverId, String content) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> UserNotFoundException.withId(receiverId));

        Notification notification = new Notification(
                receiver,
                NotificationTitle.UPDATED_ROLE.getTitle(),
                content
        );
        notificationRepository.save(notification);
    }

    public void notifyAdminOfError(String content) {
        User admin = userRepository.findByUsername(adminName)
                .orElseThrow(() -> UserNotFoundException.withUsername(adminName));

        Notification notification = new Notification(
                admin,
                NotificationTitle.FILE_UPLOAD_FAILED.getTitle(),
                content
        );

        notificationRepository.save(notification);
    }

    private List<ReadStatus> getNotificationEnabledReadStatuses(UUID channelId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(channelId);

        return readStatuses.stream()
                .filter(ReadStatus::isNotificationEnabled).collect(Collectors.toCollection(ArrayList::new));
    }
}
