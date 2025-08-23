package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannel implements ChannelService {
    final List<Channel> channelInfo = new ArrayList<>();


    @Override
    public void createChannel(Channel channel) {
        return;
    }

    @Override
    public Channel readChannel(String Channel) {
        return channelInfo.get(channelInfo.indexOf(Channel));
    }


    @Override
    public List<Channel> readAllChannels() {
        return List.of();
    }


    @Override
    public void updateChannel(Channel channel) {
        return;
    }

    @Override
    public void deleteChannel(String Channel) {
        return;
    }
}
