package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.NotificationEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

  List<NotificationEntity> findByReceiverId(UUID receiverId);

}
