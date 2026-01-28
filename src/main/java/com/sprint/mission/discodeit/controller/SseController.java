package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RequestMapping("/api/sse")
@RestController
@RequiredArgsConstructor
public class SseController {

    public final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails,
            @RequestHeader(name = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
    ){
        UUID receiverId = userDetails.getUserDto().id();

        UUID lastEventUuid = null;

        if(lastEventId.isEmpty()){
            try {
                lastEventUuid = UUID.fromString(lastEventId);
            }catch (IllegalArgumentException ignored){
            }
        }

        SseEmitter emitter = sseService.connect(receiverId,lastEventUuid);
        return ResponseEntity.status(200).body(emitter);
    }
}
