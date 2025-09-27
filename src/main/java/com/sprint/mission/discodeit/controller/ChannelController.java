package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Channel")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성", operationId = "createPublicChannel")
  @PostMapping
  public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "Private Channel 생성", operationId = "createPrivateChannel")
  @PostMapping(path = "/private")
  public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "Channel 수정", operationId = "updateChannel")
  @PutMapping(path = "{channelId}")
  public ResponseEntity<Channel> update(@PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }

  @Operation(summary = "Channel 삭제", operationId = "deleteChannel")
  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "생성된 모든 Channel을 UserId 로 조회", operationId = "findAllChannel")
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
