package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;

public record BinaryContentUpdatedEvent(
    BinaryContentDto binaryContentDto
) {}
