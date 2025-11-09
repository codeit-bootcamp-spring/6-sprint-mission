package com.sprint.mission.discodeit.messageservice;

import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageUpdate {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Test
    @DisplayName("messageUpdate:성공 감증")
    public void messageUpdate_success() {
        // given (준비)
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user =UserFixture.createUser(null);
        UserFixture.setUserId(user, userId);
        UserDto userDto = UserFixture.createUserDto(
                userId,
                user.getUsername(),
                user.getEmail(),
                null,
                false
        );

        Channel channel = ChannelFixture.publicCreateChannel("테스트","테스트");
        ChannelFixture.setChannelId(channel, channelId);

        Message message = MessageFixture.createMessage(user,channel,"테스트",new ArrayList<>());

        MessageDto messageDto = MessageFixture.createMessageDto(message,userDto,channel,new ArrayList<>());

        when(messageRepository.findById(any(UUID.class))).thenReturn(Optional.of(message));
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        //when
        MessageDto response = basicMessageService.update(messageId,"수정 테스트");

        System.out.println(response);

        //then
        assertThat(response).isEqualTo(messageDto);

        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messageMapper,times(1)).toDto(any(Message.class));
    }

    @Test
    @DisplayName("messageUpdate:메시지 없음 실패 검증")
    public void messageUpdate_fail() {
        // given (준비)
        UUID messageId = UUID.randomUUID();
        //when
        DiscodeitException exception = assertThrows(DiscodeitException.class, () -> {
            basicMessageService.update(messageId,"테스트");
        });
        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }
}
