package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDTO;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal DiscodeitUserDetails principal) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(notificationService.findAllNotificationsByUserId(principal.getUser().getId()));
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteAllNotifications(
      @PathVariable @NotNull UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails principal
  ) {

    notificationService.deleteNotificationById(notificationId, principal.getUser().getId());

    return ResponseEntity.status(204).build();

  }

}
