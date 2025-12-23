package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ChannelService {

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  Channel createPublic(CreatePublicChannelRequest createPublicChannelRequest);

  Channel createPrivate(CreatePrivateChannelRequest createPrivateChannelRequest);

  Channel find(UUID channelId);

  List<Channel> findAllByUserId(UUID channelId);

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  Channel update(UUID channelId, UpdateChannelRequest updatePublicChannelDto);

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  void delete(UUID channelId);
}
