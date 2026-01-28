package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.principal.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") UUID lastEventId
    ) {
        UUID userId = userDetails.getUserId();
        SseEmitter emitter = sseService.connect(userId, lastEventId);
        return ResponseEntity.ok(emitter);
    }
}
