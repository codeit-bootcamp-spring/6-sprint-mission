package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserRequest(
    @NotBlank(message = "user.name.required")
    @Size(max = 50)
    String username,
    @NotBlank(message = "user.email.required")
    @Email(message = "user.email.invalid")
    @Size(max = 100)
    String email,
    @Size(min = 4, max = 60, message = "user.password.size")
    String password
) {

}
