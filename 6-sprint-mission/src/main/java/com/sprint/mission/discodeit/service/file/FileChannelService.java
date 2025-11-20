package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

    Path directory = Paths.get(System.getProperty("user.dir"), "ChannelData");

    @Override
    public Channel create(String name) {
        FileInitSaveLoad.init(directory);

        Channel channel = new Channel(name);

        Path filePath = directory.resolve(channel.getCommon().getId()+".ser");
        FileInitSaveLoad.<Channel>save(filePath, channel);

        return channel;
    }

    @Override
    public Channel read(String name) {
        return allRead()
                .stream()
                .filter(ch -> ch.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Channel> allRead() {
        return FileInitSaveLoad.<Channel>load(directory);
    }

    @Override
    public Channel modify(UUID id, String name) {
        Channel channel = allRead()
                .stream()
                .filter(ch -> ch.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
        if (channel != null) {
            channel.setName(name);
            Path filePath = directory.resolve(channel.getCommon().getId()+".ser");         // 수정된 이름의 객체가 더 생길수있기에 파일명을 id.ser로 해야함
            FileInitSaveLoad.<Channel>save(filePath, channel);        // 덮어쓰기
            return channel;
        } else {
            System.out.println("해당 메시지 없음");
            return null;
        }
    }

    @Override
    public void delete(UUID id) {
        Channel channel = allRead()
                .stream().filter(ch -> ch.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
        File file = new File("Channeldata/"+channel.getCommon().getId()+".ser");
        if(file.delete()){
            //System.out.println("---delete---");
        }
    }

}
