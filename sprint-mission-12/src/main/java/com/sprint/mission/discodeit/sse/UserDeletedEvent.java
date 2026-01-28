package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record UserDeletedEvent(
    UserDto userDto
) {}
