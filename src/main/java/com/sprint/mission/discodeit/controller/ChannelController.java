package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final ChannelMapper channelMapper;

  @GetMapping
  public ResponseEntity<List<ChannelDto>> readChannelByUserId(
      @RequestParam UUID userId) {
    List<Channel> channelList = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channelMapper.toDtoList(channelList));
  }

  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublicChannel(
      @Valid @RequestBody CreatePublicChannelRequest request
  ) {
    Channel channel = channelService.createPublic(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelMapper.toDto(channel));
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @Valid @RequestBody CreatePrivateChannelRequest request
  ) {
    Channel channel = channelService.createPrivate(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(channelMapper.toDto(channel));
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> updatePublicChannel(
      @PathVariable UUID channelId,
      @Valid @RequestBody UpdateChannelRequest request
  ) {
    Channel channel = channelService.update(channelId, request);
    return ResponseEntity.ok(channelMapper.toDto(channel));
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> deleteChannel(
      @PathVariable UUID channelId
  ) {
    channelService.delete(channelId);
    return ResponseEntity.ok(channelId + " 삭제되었습니다");
  }
}
