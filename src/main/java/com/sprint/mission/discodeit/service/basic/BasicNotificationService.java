package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.config.CacheNames;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(readOnly = true)
  @Override
  @Cacheable(cacheNames = CacheNames.USER_NOTIFICATIONS, key = "#receiverId")
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 시작: receiverId={}", receiverId);
    List<NotificationDto> notificationDtoList = notificationRepository
        .findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
        .stream()
        .map(notificationMapper::toDto)
        .toList();
    log.info("알림 목록 조회 완료: receiverId={}, count={}", receiverId,
        notificationDtoList.size());
    return notificationDtoList;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("principal.userDto.id == @basicNotificationService.findReceiverId(#notificationId)")
  @Override
  public NotificationDto find(UUID notificationId) {
    log.debug("알림 단건 조회 시작: notificationId={}", notificationId);
    NotificationDto dto = notificationRepository.findById(notificationId)
        .map(notificationMapper::toDto)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
    log.info("알림 단건 조회 완료: notificationId={}", notificationId);
    return dto;
  }

  @Transactional(readOnly = true)
  public UUID findReceiverId(UUID notificationId) {
    return notificationRepository.findById(notificationId)
        .map(notification -> notification.getReceiver().getId())
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
  }
}
