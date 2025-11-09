package com.sprint.mission.discodeit.channelservice;
import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enumtype.ChannelType;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelFindByUserId {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicChannelService basicChannelService;

    @Test
    @DisplayName("ChannelFindByUserId:성공 검증")
    public void ChannelFindByUserId_success(){
        // given (준비)
        //비,공개 채널 Id
        UUID publicChannelId = UUID.randomUUID();
        UUID privateChannelId = UUID.randomUUID();

        //타켓,비공개 채널 유저
        UUID tagetUserId = UUID.randomUUID();
        UUID privateUserId = UUID.randomUUID();

        //타켓 유저
        User tagetUser = UserFixture.createUser(null);
        UserFixture.setUserId(tagetUser,tagetUserId);
        UserDto tagetUserDto = UserFixture.createUserDto(
                tagetUserId,
                tagetUser.getUsername(),
                tagetUser.getEmail(),
                null,
                true
        );
        //비공개 채널 유저
        User privateUser = UserFixture.createUser(null);
        UserFixture.setUserId(privateUser, privateUserId);
        UserDto privatUserDto = UserFixture.createUserDto(
                privateUserId,
                privateUser.getUsername(),
                privateUser.getEmail(),
                null,
                true
        );

        //공개 체널
        Channel publicChannel = ChannelFixture.publicCreateChannel("테스트","테스트글");
        ChannelFixture.setChannelId(publicChannel,publicChannelId);
        ChannelDto channelDto = ChannelFixture.createChannelDto(publicChannel,null);

        //비공개 채널
        Channel privateChannel = ChannelFixture.privateCreateChannel();
        ChannelFixture.setChannelId(privateChannel,privateChannelId);
        ChannelDto privateChannelDto = ChannelFixture.createChannelDto(privateChannel,List.of(tagetUserDto,privatUserDto));

        //타겟 읽기 상태
        ReadStatus tagetReadStatus = ReadStatus.builder()
                .user(tagetUser)
                .channel(privateChannel)
                .build();

        //비공개 채널 유저 읽기 상태
        ReadStatus privateReadStatus = ReadStatus.builder()
                .user(privateUser)
                .channel(privateChannel)
                .build();

        //공개 채널 메시지
        Message publicMessage = MessageFixture.createMessage(tagetUser,publicChannel,"공개 메시지",new ArrayList<>());
        MessageFixture.setMessageCreatedAt(publicMessage, Instant.now());

        //비공개 채널 매시지
        Message privateMessage = MessageFixture.createMessage(privateUser,privateChannel,"비공개 메시지",new ArrayList<>());
        MessageFixture.setMessageCreatedAt(privateMessage, Instant.now());


        //반횐값 설정
        when(userRepository.findById(tagetUserId)).thenReturn(Optional.of(tagetUser));
        when(channelRepository.findByType(any(ChannelType.class))).thenReturn(new ArrayList<>(List.of(publicChannel)));
        when(readStatusRepository.findByUserId(any(UUID.class))).thenReturn(List.of(tagetReadStatus));
        when(readStatusRepository.findByChannelIdIn(anyList())).thenReturn(List.of(privateReadStatus));
        when(userMapper.toDto(any(User.class))).thenReturn(privatUserDto);
        when(messageRepository.findByChannelIdInOrderByCreatedAtDesc(anyList())).thenReturn(List.of(publicMessage,privateMessage));
        when(channelMapper.toDtoList(anyList(),anyMap(),anyMap())).thenReturn(List.of(channelDto,privateChannelDto));


        //when
        List<ChannelDto> channelDtoList = basicChannelService.findAllByUserId(tagetUserId);

        //then
        //값 겁증
        assertThat(channelDtoList.size()).isEqualTo(2);
        assertThat(channelDtoList.get(0)).isEqualTo(channelDto);
        assertThat(channelDtoList.get(1)).isEqualTo(privateChannelDto);

        //행위 검증
        verify(channelRepository,times(1)).findByType(any(ChannelType.class));
        verify(readStatusRepository,times(1)).findByUserId(any(UUID.class));
        verify(readStatusRepository,times(1)).findByChannelIdIn(anyList());
        verify(userMapper,times(1)).toDto(any(User.class));
        verify(messageRepository,times(1)).findByChannelIdInOrderByCreatedAtDesc(anyList());
        verify(channelMapper,times(1)).toDtoList(anyList(),anyMap(),anyMap());
    }

    @Test
    @DisplayName("ChannelFindByUserId: 실패 검증")
    public void ChannelFindByUserId_fail(){
        UUID userId = UUID.randomUUID();

        DiscodeitException exception = assertThrows(UserNotFoundException.class ,
                ()-> basicChannelService.findAllByUserId(userId)
        );
        System.out.println(exception);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
