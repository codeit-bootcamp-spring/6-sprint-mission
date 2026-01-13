package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/notifications")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getList (
            @AuthenticationPrincipal UserDto userDto){
        List<NotificationDto> response = notificationService.list(userDto.id());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDto userDto,
            @PathVariable UUID notificationId){
        notificationService.delete(notificationId,userDto.id());
        return ResponseEntity.noContent().build();
    }


}
