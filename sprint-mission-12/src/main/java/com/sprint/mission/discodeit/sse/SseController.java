package com.sprint.mission.discodeit.sse;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @RequestParam UUID receiverId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader
  ) {
    UUID lastEventId = null;

    if (lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
      try {
        lastEventId = UUID.fromString(lastEventIdHeader);
      } catch (IllegalArgumentException ignored) {
        // 잘못된 Last-Event-ID면 그냥 복원 없이 새 연결로 처리
      }
    }

    return sseService.connect(receiverId, lastEventId);
  }
}