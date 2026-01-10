package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationResponseDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Cacheable("notifications")
    public List<NotificationResponseDto> getAllByUserId(UUID userId) {
        return notificationRepository.findAllByUser_Id(userId)
                .stream().map(NotificationMapper::toDto).toList();
    }

    @PreAuthorize("@notificationService.isOwner(#notificationId, authentication.principal.id)")
    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    public void delete(UUID notificationId) {
        notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        notificationRepository.deleteById(notificationId);
    }

    public boolean isOwner(UUID notificationId, UUID userId) {
        return notificationRepository.existsByIdAndUser_Id(notificationId, userId);
    }

}
