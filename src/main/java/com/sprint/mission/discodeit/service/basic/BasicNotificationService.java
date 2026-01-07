package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDTO;
import com.sprint.mission.discodeit.entity.NotificationEntity;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  public List<NotificationDTO> findAllNotificationsByUserId(UUID userId) {

    List<NotificationEntity> notificationEntityList = notificationRepository
        .findByReceiverId(userId);

    return notificationEntityList.stream().map(
        notificationMapper::toDTO
    ).toList();

  }

  @Override
  public void deleteNotificationById(UUID id, UUID userId) {

    notificationRepository.deleteById(id);

  }
}
