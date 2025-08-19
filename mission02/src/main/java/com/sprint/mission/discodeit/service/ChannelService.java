package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Exception.NotFoundException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFChannelInterface;
import com.sprint.mission.discodeit.service.jcf.JCFMessageInterface;

import java.util.List;
import java.util.UUID;

public class ChannelService {
    private final JCFChannelInterface jcfChannelInterface = new JCFChannelInterface();

    public void addChannel(Channel channel){
        jcfChannelInterface.addChannel(channel);
    }

    public void changeChannelName(UUID id, String channelName)
    {
        jcfChannelInterface.changeChannelName(id, channelName);
    }

    public void deleteChannel(UUID id){
        jcfChannelInterface.deleteChannel(id);
    }

    public List<Channel> getChannels(){
        return jcfChannelInterface.getAllChannel();
    }

    public Channel getChannel(String channelName){
        return  jcfChannelInterface.getChannelByName(channelName);
    }

    public void addMessage(UUID channelId, Message message){
        jcfChannelInterface.addMessage(channelId, message);
    }

}
