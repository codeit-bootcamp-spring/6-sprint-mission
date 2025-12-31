package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRoleUpdateRequest(

        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "사용자 역할은 필수입니다.")
        Role newRole
) {
}
