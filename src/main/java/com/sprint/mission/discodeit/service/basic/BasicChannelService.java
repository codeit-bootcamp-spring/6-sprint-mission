package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
<<<<<<< HEAD
import com.sprint.mission.discodeit.DTO.Channel.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.DTO.Channel.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.DTO.Channel.FindChannelDTO;
import com.sprint.mission.discodeit.DTO.Channel.UpdateChannelDTO;
import com.sprint.mission.discodeit.DTO.ReadStatus.CreateReadStatusDTO;
import com.sprint.mission.discodeit.Enum.ChannelType;
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(CreatePublicChannelDTO createPublicChannelDTO) {
        return channelRepository.save(new Channel(createPublicChannelDTO));
    }

    @Override
    public Channel createPrivate(CreatePrivateChannelDTO createPrivateChannelDTO) {
        Channel channel = new Channel(createPrivateChannelDTO.channelType());

        createPrivateChannelDTO.userIds()
                .forEach(userId ->
                        readStatusRepository.save(new ReadStatus(new CreateReadStatusDTO(userId,channel.getId()))));

        return channelRepository.save(channel);
    }

    @Override
    public FindChannelDTO find(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Channel not found"));

        Message message = messageRepository.findAll().stream()
                .filter(m -> m.getChannel().equals(id))
                .findFirst().orElseThrow(() -> new NullPointerException("Channel not found"));

        if(channel.getType().equals(ChannelType.PRIVATE)){
            return new FindChannelDTO(channel,message.getCreated(),message.getSender());
        }

        return new FindChannelDTO(channel,message.getCreated(),null);
    }

    @Override
    public List<FindChannelDTO> findAllByUserId(UUID userId) {

        //공개 채널부터 다넣음
        List<Channel> channel = channelRepository.findAll().stream()
                .filter(c -> c.getType().equals(ChannelType.PUBLIC))
                .collect(Collectors.toList());

        //비공개 채널 전채 채널 리스트에 추가
        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .forEach(rs -> {
                   Channel channel1 = channelRepository.findById(rs.getChannelId())
                           .orElseThrow(() -> new NullPointerException("Channel not found"));
                    channel.add(channel1);
                });
        //채널별 최근채팅시간 연결
        return channel.stream()
                .map(channel1 -> {
                    Message message = messageRepository.findAll().stream()
                            .filter(m -> m.getChannel().equals(channel1.getId()))
                            .findFirst().orElse(null);
                    if(message == null){
                        return new FindChannelDTO(channel1,channel1.getCreated(),userId);
                    }else {
                        return new FindChannelDTO(channel1,message.getCreated(),message.getSender());
                    }
                })
                .toList();



    }

    @Override
    public void update(UpdateChannelDTO updateChannelDTO) {
        Channel channel = channelRepository.findById(updateChannelDTO.id())
                .orElseThrow(() -> new NullPointerException("Channel not found"));
        if(channel.getType().equals(ChannelType.PRIVATE)){
            return;
        }
        channel.update(updateChannelDTO.name(), updateChannelDTO.description());
        channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(id))
                .forEach(rsDelete -> readStatusRepository.deleteById(rsDelete.getId()));

        messageRepository.findAll().stream()
                .filter(m -> m.getChannel().equals(id))
                .forEach(mDelete -> readStatusRepository.deleteById(mDelete.getId()));

        channelRepository.deleteById(id);
    }
=======
=======
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

public class BasicChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel create(String channelName, User user){
        Channel channel = new Channel(channelName,user);
        channelRepository.createChannel(channel);
        return channel;
    }


<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import com.sprint.mission.discodeit.DTO.Channel.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.DTO.Channel.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.DTO.Channel.FindChannelDTO;
import com.sprint.mission.discodeit.DTO.Channel.UpdateChannelDTO;
import com.sprint.mission.discodeit.DTO.ReadStatus.CreateReadStatusDTO;
import com.sprint.mission.discodeit.Enum.ChannelType;
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(CreatePublicChannelDTO createPublicChannelDTO) {
        return channelRepository.save(new Channel(createPublicChannelDTO));
    }

    @Override
    public Channel createPrivate(CreatePrivateChannelDTO createPrivateChannelDTO) {
        Channel channel = new Channel(createPrivateChannelDTO.channelType());

        createPrivateChannelDTO.userIds()
                .forEach(userId ->
                        readStatusRepository.save(new ReadStatus(new CreateReadStatusDTO(userId,channel.getId()))));

        return channelRepository.save(channel);
    }

    @Override
    public FindChannelDTO find(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Channel not found"));

        Message message = messageRepository.findAll().stream()
                .filter(m -> m.getChannel().equals(id))
                .findFirst().orElseThrow(() -> new NullPointerException("Channel not found"));

        if(channel.getType().equals(ChannelType.PRIVATE)){
            return new FindChannelDTO(channel,message.getCreated(),message.getSender());
        }

        return new FindChannelDTO(channel,message.getCreated(),null);
    }

    @Override
    public List<FindChannelDTO> findAllByUserId(UUID userId) {

        //공개 채널부터 다넣음
        List<Channel> channel = channelRepository.findAll().stream()
                .filter(c -> c.getType().equals(ChannelType.PUBLIC))
                .collect(Collectors.toList());

        //비공개 채널 전채 채널 리스트에 추가
        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .forEach(rs -> {
                   Channel channel1 = channelRepository.findById(rs.getChannelId())
                           .orElseThrow(() -> new NullPointerException("Channel not found"));
                    channel.add(channel1);
                });
        //채널별 최근채팅시간 연결
        return channel.stream()
                .map(channel1 -> {
                    Message message = messageRepository.findAll().stream()
                            .filter(m -> m.getChannel().equals(channel1.getId()))
                            .findFirst().orElse(null);
                    if(message == null){
                        return new FindChannelDTO(channel1,channel1.getCreated(),userId);
                    }else {
                        return new FindChannelDTO(channel1,message.getCreated(),message.getSender());
                    }
                })
                .toList();



    }

    @Override
    public void update(UpdateChannelDTO updateChannelDTO) {
        Channel channel = channelRepository.findById(updateChannelDTO.id())
                .orElseThrow(() -> new NullPointerException("Channel not found"));
        if(channel.getType().equals(ChannelType.PRIVATE)){
            return;
        }
        channel.update(updateChannelDTO.name(), updateChannelDTO.description());
        channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(id))
                .forEach(rsDelete -> readStatusRepository.deleteById(rsDelete.getId()));

        messageRepository.findAll().stream()
                .filter(m -> m.getChannel().equals(id))
                .forEach(mDelete -> readStatusRepository.deleteById(mDelete.getId()));

        channelRepository.deleteById(id);
    }
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
}
