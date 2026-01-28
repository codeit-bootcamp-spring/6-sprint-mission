package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

/**
 * 메시지가 잘 생성되었음을 알리는 이벤트
 */
public record MessageCreatedEvent (
        UserResponseDto userDto,
        ChannelResponseDto channelDto,
        MessageResponseDto messageDto
){

}
