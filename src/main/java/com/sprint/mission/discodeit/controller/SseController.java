package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @AuthenticationPrincipal DiscodeitUserDetails principal,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader,
      @RequestParam(value = "lastEventId", required = false) String lastEventIdParam) {
    UUID receiverId = principal.getUserDto().id();
    UUID lastEventId = parseEventId(lastEventIdHeader, lastEventIdParam);
    log.info("SSE 연결 요청: receiverId={}, lastEventId={}", receiverId, lastEventId);
    return sseService.connect(receiverId, lastEventId);
  }

  private UUID parseEventId(String lastEventIdHeader, String lastEventIdParam) {
    String raw = lastEventIdHeader != null ? lastEventIdHeader : lastEventIdParam;
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(raw);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
