package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.User.*;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
      UserDto create(MultipartFile multipartFile, UserCreateRequest userCreateRequest);
      List<UserDto> findAll();
      UserDto update(MultipartFile multipartFile , UUID userId, UserUpdateRequest userUpdateRequest);
      UserDto roleUpdate(UserRoleUpdateRequest userUpdateRequest);
      void delete(UUID id);
}
