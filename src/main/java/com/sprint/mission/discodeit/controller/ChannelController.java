package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/private")
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(
            @Valid @RequestBody PrivateChannelCreateRequestDto request
    ) {
        ChannelResponseDto channel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(channel);
    }

    @PostMapping("/public")
    public ResponseEntity<ChannelResponseDto> createPublicChannel(
            @Valid @RequestBody PublicChannelCreateRequestDto request
    ) {
        ChannelResponseDto channel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(channel);
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponseDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ChannelResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody PublicChannelUpdateRequestDto request
    ) {
        ChannelResponseDto response = channelService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        channelService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
