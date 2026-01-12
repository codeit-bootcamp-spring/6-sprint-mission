package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.model.NotificationDto;
import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.UpdateRoleNotificationRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;

  @Override
  public void createMessageNotification(CreateMessageNotificationRequest request) {

    log.debug("메시지 알림 생성 요청 받음: {}", request.toString());

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> {
          log.warn("Channel Not Found. channelId: {}", request.channelId());
          return new ChannelNotFoundException();
        });

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_Id(request.channelId());

    User author = readStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> user.getId().equals(request.authorId()))
        .findFirst()
        .orElseThrow(() -> {
          log.warn("작성자를 참여자 목록에서 찾을 수 없음. authorId: {}", request.authorId());
          return new UserNotFoundException();
        });

    List<Notification> notifications = readStatuses.stream()
        .filter(ReadStatus::isNotificationEnabled)
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(author.getId()))
        .map(participant -> Notification.builder()
            .sourceType(NotificationType.MESSAGE_CREATED)
            .receiverId(participant.getId())
            .sourceId(request.messageId())
            .title(author.getUsername() + " (#" + channel.getName() + ")")
            .content(request.content())
            .build())
        .toList();

    log.debug("생성된 메시지 알림 개수: {}", notifications.size());

    notificationRepository.saveAll(notifications);
  }

  @Override
  public void createRoleUpdateNotification(UpdateRoleNotificationRequest request) {

    Notification notification = Notification.builder()
        .sourceType(NotificationType.USER_ROLE_UPDATED)
        .receiverId(request.userId())
        .title("권한이 변경되었습니다.")
        .content(request.oldRole() + " -> " + request.newRole())
        .build();

    notificationRepository.save(notification);
  }

  @Override
  public List<NotificationDto> getMessageNotifications(UUID receiverId) {

    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(NotificationDto::fromEntity)
        .toList();
  }

  @Override
  // todo 메서드 보안 - 본인 알림만 삭제 가능
  public void deleteNotification(UUID notificationId, UUID userId) {

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> {
          log.warn("Notification not found. notificationId: {}", notificationId);
          return new NotificationNotFoundException();
        });

    notificationRepository.delete(notification);
  }
}
