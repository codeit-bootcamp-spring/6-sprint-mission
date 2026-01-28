package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping
    public SseEmitter connect(@RequestHeader(value = "Last-Event-ID", required = false) UUID lastEventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DiscodeitUserDetails details = (DiscodeitUserDetails) authentication.getPrincipal();
        return sseService.connect(details.getUserId(), lastEventId);
    }
}
