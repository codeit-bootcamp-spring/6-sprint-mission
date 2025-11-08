package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 테스트")
  void createPublicChannel() {
    // given
    CreatePublicChannelRequest request = CreatePublicChannelRequest.builder()
        .name("public channel")
        .description("public channel description")
        .build();

    given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Channel createChannel = channelService.createPublic(request);

    // then
    assertThat(createChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(createChannel.getName()).isEqualTo("public channel");
    assertThat(createChannel.getDescription()).isEqualTo("public channel description");
    verify(channelRepository).save(createChannel);
  }

  @Test
  @DisplayName("비공개 채널 생성 테스트")
  void createPrivateChannel() {
    // given
    UUID firstUserId = UUID.randomUUID();
    UUID secondUserId = UUID.randomUUID();

    List<UUID> participantIds = List.of(
        firstUserId,
        secondUserId
    );

    CreatePrivateChannelRequest request = CreatePrivateChannelRequest.builder()
        .participantIds(participantIds)
        .build();

    User firstUser = new User("username", "email@gmail.com", "password");
    ReflectionTestUtils.setField(firstUser, "id", firstUserId);

    User secondUser = new User("username2", "email2@gmail.com", "password");
    ReflectionTestUtils.setField(secondUser, "id", secondUserId);

    // 참가자 id는 readStatus에 저장되기에 캡처 필요
    ArgumentCaptor<ReadStatus> captor = ArgumentCaptor.forClass(ReadStatus.class);

    given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));
    given(userRepository.findById(firstUserId)).willReturn(Optional.of(firstUser));
    given(userRepository.findById(secondUserId)).willReturn(Optional.of(secondUser));
    given(readStatusRepository.save(any(ReadStatus.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Channel createChannel = channelService.createPrivate(request);

    // then
    assertThat(createChannel.getType()).isEqualTo(ChannelType.PRIVATE);
    verify(channelRepository).save(createChannel);

    // verify로 캡처한 readStatus의 userId와 channel이 참가자 id 및 생성된 채널과 일치하는지 확인
    verify(readStatusRepository, times(participantIds.size())).save(captor.capture());

    List<ReadStatus> savedReadStatuses = captor.getAllValues();

    for (int i = 0; i < participantIds.size(); i++) {
      assertThat(savedReadStatuses.get(i).getUser().getId()).isEqualTo(participantIds.get(i));
      assertThat(savedReadStatuses.get(i).getChannel()).isEqualTo(createChannel);
    }
  }

  @Test
  @DisplayName("공개 채널 업데이트 테스트")
  void updateChannel() {
    // given
    UUID channelId = UUID.randomUUID();

    UpdateChannelRequest request = UpdateChannelRequest.builder()
        .newName("new channel name")
        .newDescription("new channel description")
        .build();

    Channel channel = new Channel(ChannelType.PUBLIC, "old channel name", "old channel description");

    given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));
    given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Channel updatedChannel = channelService.update(channelId, request);

    // then
    assertThat(updatedChannel.getName()).isEqualTo("new channel name");
    assertThat(updatedChannel.getDescription()).isEqualTo("new channel description");
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("채널 삭제 테스트")
  void deleteChannel() {
    // given
    UUID channelId = UUID.randomUUID();

    List<UUID> readStatusIds = List.of(UUID.randomUUID());
    List<UUID> messageIds = List.of(UUID.randomUUID());

    ReadStatus readStatus = new ReadStatus(mock(User.class), mock(Channel.class), null);
    ReflectionTestUtils.setField(readStatus, "id", readStatusIds.get(0));

    Message message = new Message("content", mock(Channel.class), mock(User.class), Collections.emptyList());
    ReflectionTestUtils.setField(message, "id", messageIds.get(0));

    given(channelRepository.existsById(any(UUID.class))).willReturn(true);
    given(readStatusRepository.findAllByChannel_Id(channelId)).willReturn(List.of(readStatus));
    given(messageRepository.findAllByChannel_Id(channelId)).willReturn(List.of(message));

    // when
    channelService.delete(channelId);

    // then
    verify(readStatusRepository).deleteAllById(readStatusIds);
    verify(messageRepository).deleteAllById(messageIds);
    verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("사용자 ID로 채널 조회 테스트")
  void findChannelsByUserId() {
    // given
    UUID userId = UUID.randomUUID();

    Channel publicChannel = Channel.createPublic(null, null);
    Channel privateChannel = Channel.createPrivate();
    ReadStatus readStatus = new ReadStatus(mock(User.class), privateChannel, null);

    given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(readStatus));
    given(channelRepository.findAll()).willReturn(List.of(publicChannel, privateChannel));

    // when
    List<Channel> foundChannelList = channelService.findAllByUserId(userId);

    // then
    assertThat(foundChannelList).containsExactlyInAnyOrder(publicChannel, privateChannel);

    verify(readStatusRepository).findAllByUser_Id(userId);
    verify(channelRepository).findAll();
  }
}
