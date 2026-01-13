package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> findByUserId(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID userId = userDetails.getUserDto().id();
        List<NotificationDto> notificationDtoList = notificationService.findByUserId(userId);
        return ResponseEntity.ok(notificationDtoList);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(@PathVariable UUID notificationId) {
        notificationService.delete(notificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
