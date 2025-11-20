package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    // 생성자를 통해 Repository 의존성 주입
    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel read(String name) {
        return channelRepository.findall().stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
    }

    public Channel create(String name) {
        Channel channel = new Channel(name);
        channelRepository.save(channel);
        return channel;
    }

    public List<Channel> allRead() {
        return channelRepository.findall();
    }

    public Channel modify(UUID id, String name) {
        Channel channel = channelRepository.findall().stream().filter(ch -> ch.getCommon().getId().equals(id)).findAny().orElse(null);
        if (channel != null) {
            channel.setName(name);
            return channel;
        } else {
            System.out.println("해당 유저 없음");
            return null;
        }
    }

    public void delete(UUID id) {
        channelRepository.delete(id);
    }

}
