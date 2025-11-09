package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        uses = {BinaryContentMapper.class, UserMapper.class}
)
public interface MessageMapper {
    @Mapping(target = "channelId", expression = "java(message.getChannel().getId())")
    MessageDto toDto(Message message);
}
