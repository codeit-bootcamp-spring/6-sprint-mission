package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class SseController {

  private final SseService sseService;

  @GetMapping
  public SseEmitter connectSse(
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
      @AuthenticationPrincipal DiscodeitUserDetails principal
  ) {

    return sseService.connect(
        principal.getUser().getId(),
        lastEventId != null ? java.util.UUID.fromString(lastEventId) : null
    );

  }

}
