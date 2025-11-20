package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelRepository() {
        this.channelMap = new HashMap<>();
    }

    @Override
    public Channel find(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public Channel save(Channel channel) {
        UUID id = channel.getCommon().getId();
        Channel existChannel = channelMap.get(id);
        if(existChannel!=null){
            return existChannel;
        }
        channelMap.put(id,channel);
        return channel;
    }

    @Override
    public List<Channel> findall() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void delete(UUID id) {
        channelMap.remove(id);
    }

}
