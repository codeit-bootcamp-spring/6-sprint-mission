package com.sprint.mission.discodeit.messageservice;

import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Page.PageResponse;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageFindByChannelId {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Test
    @DisplayName("messageFindByChannelId:성공 검증")
    public void messageFindByChannelId_success() {
        // given (준비)
        UUID channelId = UUID.randomUUID();
        //커서 없을떄 null 커서 있을때 Instant.now()
        Instant cursor = null;
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Channel channel = ChannelFixture.publicCreateChannel("테스트", "테스트 글");
        ChannelFixture.setChannelId(channel, channelId);
        User user = UserFixture.createUser(null);
        UserDto userDto = UserFixture.createUserDto(
                UUID.randomUUID(),
                user.getUsername(),
                user.getEmail(),
                null
                , false
        );
        Message message = MessageFixture.createMessage(new User(), channel, "asdasdas", new ArrayList<>());
        MessageFixture.setMessageCreatedAt(message, Instant.now());
        MessageDto messageDto = MessageFixture.createMessageDto(message, userDto, channel, new ArrayList<>());

        Slice<Message> messageSlice = new SliceImpl<>(List.of(message));

        PageResponse<MessageDto> messageDtoPageResponse = new PageResponse<>(
                List.of(messageDto),
                Instant.now(),
                10,
                true,
                10L
        );

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        //커서 앖을때
        when(messageRepository.findByChannelIdOrderByCreatedAtDesc(any(UUID.class), any(Pageable.class))).thenReturn(messageSlice);
        //커서 있을떄
        //when(messageRepository.findByCourseIdAndIdLessThanOrderByIdDesc(any(UUID.class), any(Instant.class), any(Pageable.class))).thenReturn(messageSlice);

        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);
        //when.thenReturn은 타입 추론 못해서 값을 미리 지정하는 doReturn.when으로 사용
        doReturn(messageDtoPageResponse).when(pageResponseMapper).fromSlice(any(), nullable(Instant.class));


        //when
        PageResponse<MessageDto> response = basicMessageService.findAllByChannelId(channelId, cursor, pageRequest);

        //then
        assertThat(response).isEqualTo(messageDtoPageResponse);

        //커서 앖을때
        verify(messageRepository, times(1)).findByChannelIdOrderByCreatedAtDesc(any(UUID.class), any(Pageable.class));
        //커서 있을떄
        //verify(messageRepository, times(1)).findByCourseIdAndIdLessThanOrderByIdDesc(any(UUID.class), any(Instant.class), any(Pageable.class));

        verify(messageMapper, times(1)).toDto(any(Message.class));
        verify(pageResponseMapper, times(1)).fromSlice(any(), nullable(Instant.class));
    }

    @Test
    @DisplayName("messageFindByChannelId:체널 없음 실패 검증")
    public void messageFindByChannelId_fail() {
        // given (준비)
        UUID channelId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        //when
        DiscodeitException exception = assertThrows(ChannelNotFoundException.class, () -> {
            basicMessageService.findAllByChannelId(channelId,null, pageRequest);
        });
        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND);
    }
}
