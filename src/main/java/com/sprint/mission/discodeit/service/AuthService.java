package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;

public interface AuthService {

    UserDto updateRole(RoleUpdateRequest request);

    UserDto updateRoleInternal(RoleUpdateRequest request);

    JwtDto refreshAccessToken(String refreshToken);
}
