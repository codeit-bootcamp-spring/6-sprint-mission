package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "profile", source = "user.profileImage")
    @Mapping(target = "online", expression = "java(user.getUserStatus() != null && user.getUserStatus().isOnline())")
    UserResponseDto toDto(User user);

}
