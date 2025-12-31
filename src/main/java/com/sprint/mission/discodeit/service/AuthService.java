package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AuthService {

  boolean isOwner(UUID id, Object principal);

  boolean isMessageAuthor(UUID messageId, Object principal);

  @PreAuthorize("hasRole('ADMIN')")
  User updateRole(RoleUpdateRequest updateRequest);
}
