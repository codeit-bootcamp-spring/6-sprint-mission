package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
<<<<<<< HEAD
<<<<<<< HEAD
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
@Repository
public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channelsDate;

    public JCFChannelRepository() {

        channelsDate = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        int idx = channelsDate.indexOf(channel);
        if (idx >=0) {
            channelsDate.set(idx, channel);
        }else {
            channelsDate.add(channel);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {

        return channelsDate.stream()
                .filter(channel -> channel.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(channelsDate);
    }

    @Override
    public boolean existsById(UUID id) {
        return channelsDate.stream()
                .anyMatch(channel -> channel.getId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {
        channelsDate.removeIf(channel -> channel.getId().equals(id));
=======
=======
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channels;

    public JCFChannelRepository() {

        channels = new ArrayList<>();
    }

    public void createChannel(Channel channel) {
        channels.add(channel);
    }

    public Channel findChannelById(UUID id) {

        return channels.stream()
                .filter(channel -> channel.getChannelId().equals(id))
                .findAny()
                .orElse(null);
    }

    public List<Channel> findAllChannels() {
        return channels;
    }

    public void updateChannel(Channel channel) {
        int idx = channels.indexOf(channel);
        if (idx == -1) {
            throw new NullPointerException("해당 채널을 없습니다");
        }
        channels.set(idx, channel);

    }

    public void deleteChannel(UUID id) {
        channels.remove(findChannelById(id));
    }

    public void addMessageToChannel(Channel channel, Message message) {
        channel.updateChanelMessages(message);
        updateChannel(channel);
    }

    public void removeMessageFromChannel(Channel channel, Message message) {

<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
@Repository
public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channelsDate;

    public JCFChannelRepository() {

        channelsDate = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        int idx = channelsDate.indexOf(channel);
        if (idx >=0) {
            channelsDate.set(idx, channel);
        }else {
            channelsDate.add(channel);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {

        return channelsDate.stream()
                .filter(channel -> channel.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(channelsDate);
    }

    @Override
    public boolean existsById(UUID id) {
        return channelsDate.stream()
                .anyMatch(channel -> channel.getId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {
        channelsDate.removeIf(channel -> channel.getId().equals(id));
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    }
}
