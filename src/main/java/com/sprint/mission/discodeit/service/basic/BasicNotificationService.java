package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.Role;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void create(UUID userId, String title, String content) {
        Notification notification = Notification.builder()
                .receiverId(userId)
                .title(title)
                .content(content)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createToAdmins(String title, String content) {
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        List<Notification> notifications =new ArrayList<>();
        for (User user : admins){
            Notification notification = Notification.builder()
                    .receiverId(user.getId())
                    .title(title)
                    .content(content)
                    .build();
            notifications.add(notification);
        }
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> list(UUID userId) {
        return notificationRepository.findAllByReceiverId(userId);
    }

    @Override
    @Transactional
    public void delete(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
