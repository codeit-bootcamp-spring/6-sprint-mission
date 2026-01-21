package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.model.ChannelDto;
import com.sprint.mission.discodeit.dto.model.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {

    List<UserDto> participants = new ArrayList<>();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      participants = readStatusRepository.findAllByChannel_Id(channel.getId()).stream()
          .map(readStatus -> userMapper.toDto(readStatus.getUser()))
          .toList();
    }

    Instant lastMessageAt = messageRepository.findAllByChannel_Id(channel.getId()).stream()
        .map(BaseUpdatableEntity::getUpdatedAt)
        .max(Comparator.naturalOrder())
        .orElse(channel.getCreatedAt());

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }

  public List<ChannelDto> toDtoList(List<Channel> channelList) {
    return channelList.stream()
        .map(this::toDto)
        .toList();
  }
}
