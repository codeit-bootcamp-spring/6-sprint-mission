package com.sprint.mission.discodeit.channelservice;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelUpdate {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private BasicChannelService basicChannelService;

    @Test
    @DisplayName("updateChannel:성공검증")
    public void updateChannel_success() {
        // given (준비)
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest publicChannelUpdateRequest = PublicChannelUpdateRequest.builder()
                .newName("수정 테스트")
                .newDescription("수정 태스트")
                .build();

        Channel channel = ChannelFixture.publicCreateChannel("기존 채널","기본 글");
        Channel updateChannel = ChannelFixture.publicCreateChannel("수정 채널","수정 글");
        ChannelFixture.setChannelId(channel,channelId);
        ChannelFixture.setChannelId(updateChannel,channelId);

        ChannelDto channelDto = ChannelFixture.createChannelDto(updateChannel,new ArrayList<>());

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(channelRepository.save(channel)).thenReturn(updateChannel);
        when(channelMapper.toDto(any(Channel.class),anyList(),isNull(Instant.class))).thenReturn(channelDto);
        when(messageRepository.findTopByChannelIdOrderByCreatedAtDesc(any(UUID.class))).thenReturn(Optional.of(new Message()));

        //when
        ChannelDto response = basicChannelService.update(channelId,publicChannelUpdateRequest);

        //then
        assertThat(response).isEqualTo(channelDto);
        verify(channelRepository,times(1)).findById(channelId);
        verify(channelRepository,times(1)).save(channel);
        verify(channelMapper,times(1)).toDto(any(Channel.class),anyList(),isNull(Instant.class));
        verify(messageRepository,times(1)).findTopByChannelIdOrderByCreatedAtDesc(any(UUID.class));

    }

    @Test
    @DisplayName("updateChannel:채널 없음 실패 검증")
    public void updateChannel_fail() {
        UUID channelId = UUID.randomUUID();
        DiscodeitException exception = assertThrows(ChannelNotFoundException.class , ()->{
            basicChannelService.update(channelId,PublicChannelUpdateRequest.builder().build());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND);
    }

}
