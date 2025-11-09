package com.sprint.mission.discodeit.channelservice;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import com.sprint.mission.discodeit.support.ReadStatusFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChannelDelete {
    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @InjectMocks
    private BasicChannelService basicChannelService;


    @Test
    @DisplayName("channelDelete:성공 검증")
    public void channelDelete_success() {
        // given (준비)
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Channel channel = ChannelFixture.publicCreateChannel("기본", "기본");
        ChannelFixture.setChannelId(channel, channelId);
        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userId);

        List<Message> messages = List.of(Message.builder()
                .channel(channel)
                .author(user)
                .content("메시지")
                .attachment(new ArrayList<>())
                .build()
        );
        MessageFixture.setMessageId(messages.get(0),UUID.randomUUID());
        List<ReadStatus> readStatuses = List.of(ReadStatus.builder()
                .channel(channel)
                .user(user)
                .build()
        );
        ReadStatusFixture.setReadStatusId(readStatuses.get(0), UUID.randomUUID());

        when(readStatusRepository.findAll()).thenReturn(readStatuses);
        when(messageRepository.findAll()).thenReturn(messages);
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));



        //when
        basicChannelService.delete(channelId);

        //then
        verify(readStatusRepository, times(1)).findAll();
        verify(readStatusRepository, times(1)).deleteById(any(UUID.class));

        verify(messageRepository, times(1)).findAll();
        verify(messageRepository, times(1)).deleteById(any(UUID.class));
        verify(channelRepository, times(1)).deleteById(any(UUID.class));

    }

    @Test
    @DisplayName("channelDelete:채널 없음 실패 검증")
    public void deleteChannel_fail() {
        UUID channelId = UUID.randomUUID();

        DiscodeitException exception = assertThrows(ChannelNotFoundException.class , ()->{
            basicChannelService.delete(channelId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND);
    }
}
