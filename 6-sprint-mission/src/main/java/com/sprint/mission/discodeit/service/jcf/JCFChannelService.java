package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    /*

  싱글톤 예시

    private static final JCFChannelService INSTANCE = new JCFChannelService();

    private JCFChannelService() {
        this.channelData = new ArrayList<>();
    }

    public static JCFChannelService getInstance(){
        return INSTANCE;
    }

 DI 예시

    private final UserService userService;
    private final MessageService messageService;

    public JCFChannelService(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
        this.channelData = new ArrayList<>();
    }
*/
    private final List<Channel> channelData;

    public JCFChannelService(){
        this.channelData = new ArrayList<>();
    }

    @Override
    public Channel read(String name) {
        return channelData.stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        channelData.add(channel);
        return channel;
    }

    @Override
    public List<Channel> allRead() {
        return channelData;
    }

    @Override
    public Channel modify(UUID id, String name) {
        Channel channel =  channelData.stream().filter(ch -> ch.getCommon().getId().equals(id)).findAny().orElse(null);
        if(channel!=null){
            channel.setName(name);
            return channel;
        } else{
            System.out.println("해당 메시지 없음");
            return null;
        }
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelData.stream().filter(ch -> ch.getCommon().getId().equals(id)).findAny().orElse(null);
        channelData.remove(channel);
    }

}
