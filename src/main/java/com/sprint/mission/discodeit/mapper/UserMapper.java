package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "profile", source = "user.profileImage")
    // TODO online 처리?
    UserResponseDto toDto(User user);

}
