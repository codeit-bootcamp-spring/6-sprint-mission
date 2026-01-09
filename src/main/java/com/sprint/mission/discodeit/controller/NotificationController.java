package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/notifications")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    @GetMapping
    public ResponseEntity<NotificationDto> getList (){
        return null;
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<NotificationDto> notificationCheck(@PathVariable String notificationId){
        return null;
    }


}
