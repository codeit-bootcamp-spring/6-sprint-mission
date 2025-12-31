package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PrivateChannelUpdateException extends ChannelException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PrivateChannelUpdateException(UUID channelId) {
        super(
                ErrorCode.PRIVATE_CHANNEL_UPDATE,
                "비공개 채널 정보는 수정할 수 없습니다. channelId=" + channelId,
                Map.of("channelId", channelId)
        );
    }
}
