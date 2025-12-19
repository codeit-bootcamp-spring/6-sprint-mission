package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // User의 profile을 BinaryContentMapper.todto를 이용해 UserDto의 profile로 매핑
  @Mapping(source = "profile", target = "profile", qualifiedByName = "binaryContentToDto")
  @Mapping(source = "userStatus.online", target = "online")
  @Named("userToDto")
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> userList);
}
