package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  User create(CreateUserRequest createUserRequest, MultipartFile profile);

  User find(UUID userId);

  List<User> findAll();

  @PreAuthorize("@basicAuthService.isOwner(#userId, principal)")
  User update(UUID userId, UpdateUserRequest updateUserRequest, MultipartFile profile);

  @PreAuthorize("@basicAuthService.isOwner(#userId, principal) or hasRole('ADMIN')")
  void delete(UUID userId);
}
