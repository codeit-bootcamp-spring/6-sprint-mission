package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.userDetails.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    @Lazy
    private SessionRegistry sessionRegistry;

    @Mapping(target = "online", expression = "java(isOnline(user))")
    abstract public UserDto toDto(User user);


    protected boolean isOnline(User user) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof DiscodeitUserDetails userDetails) {
                if (userDetails.getUserDto().id().equals(user.getId())) {
                    return !sessionRegistry.getAllSessions(userDetails, false).isEmpty();
                }
            }
        }
        return false;
    }
}
