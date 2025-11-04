package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelAlreadyExistsException extends ChannelException {

  public ChannelAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_CHANNEL);
  }
}
