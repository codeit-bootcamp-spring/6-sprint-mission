package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelCreatePublicDto dto);

    Channel create(ChannelCreatePrivateDto dto);

    Channel find(UUID channelId);

    List<Channel> findAllByUserId(UUID userId);

    Channel update(UUID channelId, ChannelUpdateDto dto);

    void delete(UUID channelId);
}