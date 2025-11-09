package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.Channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "Channel", description = "Channel API")
@RequestMapping("/api/channels")
@RestController
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;


    @PostMapping("/public")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PublicChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublic(request));
    }


    @PostMapping("/private")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPrivate(request));
    }


    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request) {
        return ResponseEntity.status(200).body(channelService.update(channelId, request));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(
            @RequestParam("userId") UUID id) {
        List<ChannelDto> findChannelDTOS = channelService.findAllByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(findChannelDTOS);
    }
}
