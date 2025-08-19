package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Exception.DuplicateNameException;
import com.sprint.mission.discodeit.Exception.NotFoundException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelInterface {
    private final List<Channel> channels;

    public JCFChannelInterface() {
        channels = new ArrayList<>();
    }

    public List<Channel> getAllChannel() {return channels;}

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public Channel getChannelByID(UUID id) throws NotFoundException {
        return channels.stream().filter(channel -> channel.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException("채널을 찾을 수 없습니다."));
    }

    public Channel getChannelByName(String channelName) throws NotFoundException, DuplicateNameException {
        if(channels.stream().filter(n -> n.getChannelName().equals(channelName)).count() > 1){
            throw new DuplicateNameException(channelName);
        }
        return channels.stream().filter(n -> n.getChannelName().equals(channelName))
                .findFirst().orElseThrow(() -> new NotFoundException("해당하는 채널을 찾을 수 없습니다."));
    }

    public void changeChannelName(UUID id, String channelName) throws NotFoundException,  DuplicateNameException {
        if(channels.stream().filter(n -> n.getChannelName().equals(channelName)).count() > 1){
            throw new DuplicateNameException(channelName);
        }
        Channel channel = getChannelByID(id);
        channel.setUpdatedAt(System.currentTimeMillis());
        channel.setChannelName(channelName);
    }

    public void deleteChannel(UUID id) throws NotFoundException {
        if(channels.stream().noneMatch(n -> n.getId().equals(id))){
            throw new NotFoundException("채널이 존재하지 않습니다.");
        }
        channels.removeIf(n -> n.getId().equals(id));
    }

    public void addMessage(UUID id, Message message) throws NotFoundException {
        Channel channel = channels.stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
        if(channel == null){
            throw new NotFoundException("채널을 찾을 수 없습니다.");
        }
        channel.setMessages(message);
    }

}
