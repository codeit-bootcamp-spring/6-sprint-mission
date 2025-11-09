package com.sprint.mission.discodeit.channelservice;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.support.ChannelFixture;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelCreate {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @InjectMocks
    private BasicChannelService basicChannelService;

    @Test
    @DisplayName("성공 검증")
    public void createPublic_success() {
        // given (준비)
        //공개 채널 생성 요청값 설정
        PublicChannelCreateRequest publicChannelCreateRequest = PublicChannelCreateRequest.builder()
                .name("공개채널")
                .description("테스트생성")
                .build();

        Channel channel = new Channel("공개채널", "테스트생성");
        ChannelFixture.setChannelId(channel, UUID.randomUUID());

        ChannelDto channelDto = ChannelFixture.createChannelDto(channel, new ArrayList<>());

        when(channelRepository.save(any(Channel.class))).thenReturn(channel);
        when(channelMapper.toDto(any(Channel.class), anyList(), isNull(Instant.class))).thenReturn(channelDto);

        //when
        ChannelDto response = basicChannelService.createPublic(publicChannelCreateRequest);

        //then
        assertThat(response).isEqualTo(channelDto);

        verify(channelRepository, times(1)).save(any(Channel.class));
        verify(channelMapper, times(1)).toDto(any(Channel.class), anyList(), isNull(Instant.class));
    }

    @Test
    @DisplayName("createCPrivate:성공 검증")
    public void createCPrivate_success() {
        // given (준비)
        //비공개 채널 생성 요청값 설정
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest privateChannelCreateRequest = PrivateChannelCreateRequest.builder()
                .participantIds(userIds)
                .build();
        //비공개 채널 설정
        Channel channel = new Channel();

        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userIds.get(0));

        UserDto userDto = UserFixture.createUserDto(
                userIds.get(0),
                "user",
                "test@test",
                null,
                false
        );


        ChannelDto channelDto = ChannelFixture.createChannelDto(channel, List.of(userDto));

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userMapper.toDtoList(anyList())).thenReturn(List.of(userDto));
        when(channelMapper.toDto(any(Channel.class), anyList(), isNull(Instant.class))).thenReturn(channelDto);

        //when
        ChannelDto response = basicChannelService.createPrivate(privateChannelCreateRequest);

        //then
        assertThat(response).isEqualTo(channelDto);
        verify(userRepository, times(2)).findById(any(UUID.class));
        verify(readStatusRepository, times(1)).saveAll(anyList());
        verify(channelRepository, times(1)).save(any(Channel.class));
        verify(channelMapper, times(1)).toDto(any(Channel.class), anyList(), isNull(Instant.class));
    }

    public void createPublic_fail() {
    }

    @Test
    @DisplayName("createCPrivate:실패 검증")
    public void createCPrivate_fail() {
        // given (준비)
        //비공개 채널 생성 요청값 설정
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest privateChannelCreateRequest = PrivateChannelCreateRequest.builder()
                .participantIds(userIds)
                .build();

        //when
        DiscodeitException exception = assertThrows(UserNotFoundException.class,
                () -> basicChannelService.createPrivate(privateChannelCreateRequest)
        );

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

}
