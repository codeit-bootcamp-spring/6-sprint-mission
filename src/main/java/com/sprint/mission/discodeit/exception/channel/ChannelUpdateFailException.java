package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;


public class ChannelUpdateFailException extends ChannelException {
    public ChannelUpdateFailException() {
        super(ErrorCode.CHANNEL_UPDATE_FAIL);
    }
}