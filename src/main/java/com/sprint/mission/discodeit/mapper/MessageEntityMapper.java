package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDTO;
import com.sprint.mission.discodeit.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MessageEntityMapper {

  @Mappings({
      @Mapping(target = "channelId", source = "channel.id"),
      @Mapping(target = "author", source = "author"),
      @Mapping(target = "attachments", source = "attachments", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
  })
  MessageDTO.Message toMessage(MessageEntity messageEntity);

  @Mappings({
      //@Mapping(target = "channel", source = "channelId"),
      @Mapping(target = "author", source = "author"),
      //@Mapping(target = "attachments", source = "attachments")
  })
  MessageEntity toEntity(MessageDTO.Message messageDTO);

}
