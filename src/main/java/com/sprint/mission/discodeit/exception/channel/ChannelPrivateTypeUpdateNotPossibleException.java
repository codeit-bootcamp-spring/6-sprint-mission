package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelPrivateTypeUpdateNotPossibleException extends ChannelException{
    public ChannelPrivateTypeUpdateNotPossibleException() {
        super(ErrorCode.CHANNEL_PRIVATE_TYPE_UPDATE_NOT_POSSIBLE);
    }
}
