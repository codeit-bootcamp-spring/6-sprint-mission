package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.model.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  @Mapping(source = "channel.id", target = "channelId")
  @Mapping(source = "author", target = "author", qualifiedByName = "userToDto")
  @Mapping(target = "attachments", source = "attachments", qualifiedByName = "binaryContentToDtoList")
  MessageDto toDto(Message message);
}

//@Component
//@RequiredArgsConstructor
//public class MessageMapper {
//
//  private final BinaryContentMapper binaryContentMapper;
//  private final UserMapper userMapper;
//
//  public MessageDto toDto(Message message) {
//    return new MessageDto(
//        message.getId(),
//        message.getCreatedAt(),
//        message.getUpdatedAt(),
//        message.getContent(),
//        message.getChannel().getId(),
//        userMapper.toDto(message.getAuthor()),
//        binaryContentMapper.toDtoList(message.getAttachments())      // 1대다 관계에서 lazy로 설정하면 lazy 에러 발생
//    );
//  }
//}
