package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    UserStatusDto toDto(UserStatus userStatus);

    List<UserStatusDto> toDtoList(List<UserStatus> userStatuses);
}
