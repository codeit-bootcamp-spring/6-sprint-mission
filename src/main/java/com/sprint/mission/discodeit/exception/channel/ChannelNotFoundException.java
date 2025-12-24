package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChannelNotFoundException extends DiscodeitException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ChannelNotFoundException(UUID channelId) {
        super(
                ErrorCode.CHANNEL_NOT_FOUND,
                "채널을 찾을 수 없습니다. channelId=" + channelId,
                Map.of("channelId", channelId)
        );
    }
}
