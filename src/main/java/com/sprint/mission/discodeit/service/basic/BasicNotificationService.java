package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.dto.model.NotificationDto;
import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateRoleNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateStorageNotificationRequest;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final SseService sseService;

  @Override
  public void createSseMessageNotification(CreateMessageNotificationRequest request) {

    List<UUID> receiverIds = readStatusRepository.findAllByChannel_Id(request.channelId()).stream()
        .filter(ReadStatus::isNotificationEnabled)
        .map(rs -> rs.getUser().getId())
        .filter(id -> !id.equals(request.authorId()))
        .toList();

    Map<String, String> sseData = Map.of(
        "title", request.authorName() + " (#" + request.channelName() + ")",
        "content", request.content()
    );

    sseService.send(receiverIds, "notifications.created", sseData);
  }

  @Override
  public void createPersistentMessageNotification(CreateMessageNotificationRequest request) {

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_Id(request.channelId());

    List<Notification> notifications = readStatuses.stream()
        .filter(ReadStatus::isNotificationEnabled)
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(request.authorId()))
        .map(user -> Notification.builder()
            .sourceType(NotificationType.MESSAGE_CREATED)
            .receiverId(user.getId())
            .title(request.authorName() + " (#" + request.channelName() + ")")
            .content(request.content())
            .build())
        .toList();

    notificationRepository.saveAll(notifications);
  }

  @Override
  public void createRoleUpdateNotification(CreateRoleNotificationRequest request) {

    Notification notification = Notification.builder()
        .sourceType(NotificationType.USER_ROLE_UPDATED)
        .receiverId(request.userId())
        .title("권한이 변경되었습니다.")
        .content(request.oldRole() + " -> " + request.newRole())
        .build();

    notificationRepository.save(notification);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificationDto> getMessageNotifications(UUID receiverId) {

    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(NotificationDto::from)
        .toList();
  }

  @Override
  public void createStorageNotification(CreateStorageNotificationRequest request) {

    User admin = userRepository.findByRole(Role.ADMIN).stream()
        .findFirst()
        .orElseThrow(() -> {
          log.warn("Admin user not found.");
          return new UserNotFoundException();
        });

    Notification notification = Notification.builder()
        .sourceType(NotificationType.STORAGE_PUT_FAILED)
        .receiverId(admin.getId())
        .title("스토리지 저장 실패")
        .content("RequestId: " + request.requestId() +
            ", BinaryContentId: " + request.binaryContentId() +
            ", ErrorType: " + request.errorType() +
            ", ErrorMessage: " + request.errorMessage())
        .build();

    notificationRepository.save(notification);
  }

  @Override
  public void deleteNotification(UUID notificationId, UUID userId) {

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> {
          log.warn("Notification not found. notificationId: {}", notificationId);
          return new NotificationNotFoundException();
        });

    notificationRepository.delete(notification);
  }
}
