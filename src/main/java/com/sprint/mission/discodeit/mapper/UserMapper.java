package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.model.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  protected JwtRegistry jwtRegistry;

  // User의 profile을 BinaryContentMapper.todto를 이용해 UserDto의 profile로 매핑
  @Mapping(source = "profile", target = "profile", qualifiedByName = "binaryContentToDto")
  @Mapping(target = "online", expression = "java(jwtRegistry.hasActiveJwtInformationByUserId(user.getId()))")
  @Named("userToDto")
  public abstract UserDto toDto(User user);

  public abstract List<UserDto> toDtoList(List<User> userList);
}
