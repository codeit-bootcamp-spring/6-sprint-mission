package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping("/public")
    public Channel createPublicChannel(@RequestBody ChannelCreatePublicDto dto) {
        return channelService.create(dto);
    }

    @PostMapping("/private")
    public Channel createPrivateChannel(@RequestBody ChannelCreatePrivateDto dto) {
        return channelService.create(dto);
    }

    @GetMapping("/{channelId}")
    public Channel findChannel(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }

    @GetMapping("/user/{userId}")
    public List<Channel> findAllByUserId(@PathVariable UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    @PutMapping("/{channelId}")
    public Channel updateChannel(@PathVariable UUID channelId, @RequestBody ChannelUpdateDto dto) {
        return channelService.update(channelId, dto);
    }

    @DeleteMapping("/{channelId}")
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }
}