package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BinaryContentMapper.class},
        imports = {Instant.class}
)
public abstract class UserMapper {

    @Autowired
    protected SessionManager sessionManager;

    @Mapping(target = "online", expression = "java(sessionManager.hasActiveSessions(user.getId()))")
    public abstract UserDto toDto(User user);

    public abstract List<UserDto> toDtoList(List<User> users);
}
