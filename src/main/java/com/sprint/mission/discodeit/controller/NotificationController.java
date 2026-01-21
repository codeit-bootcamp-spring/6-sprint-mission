package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.model.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getMessageNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    List<NotificationDto> dtoList = notificationService.getMessageNotifications(userDetails.getUserDto().id());
    return ResponseEntity.ok(dtoList);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    notificationService.deleteNotification(notificationId, userDetails.getUserDto().id());
    return ResponseEntity.noContent().build();
  }
}
