package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.cache.CacheNames;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.cache.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationCacheService notificationCacheService;

    @Cacheable(value = CacheNames.USER_NOTIFICATIONS, key = "#userId")
    @Override
    public List<NotificationDto> findByUserId(UUID userId) {
        List<Notification> notifications = notificationRepository.findByReceiverId(userId);
        return notifications.stream().map(notificationMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void delete(UUID notificationId) {
        Notification foundNotification = notificationRepository.findById(notificationId).orElse(null);
        if (foundNotification == null) {
            return;
        }

        if (isOwner(foundNotification)) {
            notificationRepository.deleteById(notificationId);
            notificationCacheService.evictForUser(foundNotification.getReceiver().getId());
        }
    }

    private boolean isOwner(Notification notification) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            return userDetails.getUserDto().id().equals(notification.getReceiver().getId());
        }

        return false;
    }
}
