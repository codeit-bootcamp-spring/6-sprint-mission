package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;

public record ChannelCreatedEvent(
    ChannelDto channelDto
) {}
