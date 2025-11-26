package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.enums.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(resolveParticipants(channel))")
    @Mapping(target = "lastMessageSentAt", expression = "java(resolveLastMessageAt(channel))")
    abstract public ChannelResponseDto toDto(Channel channel);

    protected Instant resolveLastMessageAt(Channel channel) {
        return messageRepository.findLastMessageAtByChannelId(
                        channel.getId())
                .orElse(Instant.MIN);
    }

    protected List<UserResponseDto> resolveParticipants(Channel channel) {
        List<UserResponseDto> participants = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelIdWithUser(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .forEach(participants::add);
        }
        return participants;
    }
}
