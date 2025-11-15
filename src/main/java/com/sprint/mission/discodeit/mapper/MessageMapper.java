package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

    // @Mapping(target = "author", ignore = true) // 서비스단에서 결정
    // @Mapping(target = "attachments", ignore = true) // 서비스단에서 결정
    @Mapping(target = "channelId", source = "channel.id")
    MessageResponseDto toDto(Message message);

//    public static MessageResponseDto toDto(Message message) {
//
//        Boolean isUserOnline = userStatusRepository.findByUserId(message.getUser().getId())
//                    .map(UserStatus::isOnline)
//                    .orElse(false);
//
//        List<BinaryContentResponseDto> attachmentDtos = message.getBinaryContents().stream()
//                    .map(BinaryContentMapper::toDto)
//                    .toList();
//
//        return MessageResponseDto.builder()
//                .id(message.getId())
//                .author(UserMapper.toDto(message.getUser(), isUserOnline))
//                .channelId(message.getChannel().getId())
//                .content(message.getContent())
//                .attachments(attachmentDtos)
//                .build();
//    }
}
