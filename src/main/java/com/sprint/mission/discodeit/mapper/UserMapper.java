package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BinaryContentMapper.class},
        imports = {Instant.class}
)
public interface UserMapper {

    @Mapping(target = "online", expression = "java(user.getStatus().isConnecting(Instant.now()))")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
