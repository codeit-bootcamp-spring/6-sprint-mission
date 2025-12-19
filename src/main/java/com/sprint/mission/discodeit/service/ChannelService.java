package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublic(CreatePublicChannelRequest createPublicChannelRequest);

  Channel createPrivate(CreatePrivateChannelRequest createPrivateChannelRequest);

  Channel find(UUID channelId);

  List<Channel> findAllByUserId(UUID channelId);

  Channel update(UUID channelId, UpdateChannelRequest updatePublicChannelDto);

  void delete(UUID channelId);
}
