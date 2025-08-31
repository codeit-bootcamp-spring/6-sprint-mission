package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.repository.ChannelRepositoryInterface;
import com.sprint.mission.discodeit.service.repository.UserChannelRepositoryInterface;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepositoryInterface channelRepository;
    private final UserChannelRepositoryInterface userChannelRepository;

    private MessageService messageService;

    // 생성자 - 생성 시 레포지토리 주입
    public BasicChannelService(ChannelRepositoryInterface channelRepository,
                               UserChannelRepositoryInterface userChannelRepository,
                               MessageService messageService) {
        this.channelRepository = channelRepository;
        this.userChannelRepository = userChannelRepository;
        this.messageService = messageService;
    }

    // 채널 생성
    @Override
    public Channel createChannel(String name, String information) {
        return channelRepository.createChannel(name, information);
    }

    // 전체 채널 조회
    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    // 특정 유저의 모든 채널 조회
    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId) {
        List<UUID> channels = userChannelRepository.findChannelListOfUserId(userId);
        return channels.stream().map(channelRepository::findById).toList();
    }

    // 채널 이름 수정
    @Override
    public boolean updateName(Channel channel, String updatedName) {
        if(channelRepository.findById(channel.getId()) == null) return false;
        channelRepository.updateName(channel, updatedName);
        messageService.modifyChannelName(channel.getId(), updatedName);
        return true;
    }

    // 채널 정보 수정
    @Override
    public boolean updateInformation(Channel channel, String updateInformation) {
        if(channelRepository.findById(channel.getId()) == null) return false;
        channelRepository.updateInformation(channel, updateInformation);
        return true;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(Channel channel) {
        if(channelRepository.findById(channel.getId()) == null) return false;
        // 채널 존재 시
        messageService.deleteAllMessagesInChannel(channel.getId());
        userChannelRepository.removeAllOfChannel(channel.getId());
        channelRepository.deleteChannel(channel);
        return true;
    }
}
