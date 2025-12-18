package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.mapper.ChannelEntityMapper;
import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 테스트")
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelEntityMapper channelEntityMapper;
  @Mock
  private UserEntityMapper userEntityMapper;
  @Mock
  private BasicChannelService.ChannelWithParticipants channelWithParticipants;

  @InjectMocks
  private BasicChannelService basicChannelService;

  private final UUID testChannelId = UUID.randomUUID();
  private final String testChannelName = "test-channel";
  private final String testDescription = "Test Channel";
  private ChannelEntity testChannelEntity;
  private ChannelDTO.Channel testChannelDto;

  @BeforeEach
  void setUp() {

    testChannelEntity = ChannelEntity.builder()
        .name(testChannelName)
        .description(testDescription)
        .type(ChannelType.PUBLIC)
        .build();

    testChannelDto = ChannelDTO.Channel.builder()
        .id(testChannelId)
        .name(testChannelName)
        .description(testDescription)
        .type(ChannelType.PUBLIC)
        .build();

  }

  @Test
  @DisplayName("채널 생성 성공 테스트")
  void createChannel_Success() {

    //given
    ChannelDTO.CreatePublicChannelCommand command = new ChannelDTO.CreatePublicChannelCommand(
        testChannelName, testDescription);

    when(channelRepository.save(any(ChannelEntity.class)))
        .thenReturn(testChannelEntity);
    when(channelEntityMapper.toChannel(any(ChannelEntity.class)))
        .thenReturn(testChannelDto);

    //when
    ChannelDTO.Channel result = basicChannelService.createChannel(command);

    //then
    assertNotNull(result);
    assertEquals(testChannelName, result.getName());

  }

  @Test
  @DisplayName("채널 비공개 생성 성공 테스트")
  void createPrivateChannel_Success() {

    //given
    ChannelDTO.CreatePrivateChannelCommand command = new ChannelDTO.CreatePrivateChannelCommand(
        ChannelType.PRIVATE, List.of(), testDescription);

    when(channelRepository.save(any(ChannelEntity.class)))
        .thenReturn(testChannelEntity);
    when(channelEntityMapper.toChannel(any(ChannelEntity.class)))
        .thenReturn(testChannelDto);

    //when
    ChannelDTO.Channel result = basicChannelService.createPrivateChannel(command);

    //then
    assertNotNull(result);
    assertEquals(testDescription, result.getDescription());

  }

  @Test
  @DisplayName("채널 ID로 조회 성공 테스트")
  void findChannelById_Success() {

    //given
    when(channelRepository.findById(testChannelId))
        .thenReturn(Optional.of(testChannelEntity));
    when(channelEntityMapper.toChannel(any(ChannelEntity.class)))
        .thenReturn(testChannelDto);
    //when(channelWithParticipants.addParticipantsToChannel(testChannelEntity))
        //.thenReturn(testChannelDto);

    //when
    ChannelDTO.Channel result = basicChannelService.findChannelById(testChannelId);

    //then
    assertNotNull(result);
    assertEquals(testChannelId, result.getId());

  }

  @Test
  @DisplayName("채널 ID로 조회 실패 테스트 - 존재하지 않는 채널")
  void findChannelById_Fail_noSuchChannel() {

    //given
    when(channelRepository.findById(testChannelId))
        .thenReturn(Optional.empty());

    //when & then
    assertThrows(NoSuchChannelException.class, () -> {
      basicChannelService.findChannelById(testChannelId);
    });

  }

  @Test
  @DisplayName("채널 업데이트 성공 테스트")
  void updateChannel_Success() {

    //given
    String updatedName = "updated-name";
    String updatedDescription = "Updated Description";
    ChannelDTO.UpdateChannelCommand command = new ChannelDTO.UpdateChannelCommand(
        testChannelId, updatedName, ChannelType.PUBLIC, null, updatedDescription);

    when(channelRepository.existsById(testChannelId))
        .thenReturn(true);
    when(channelRepository.findById(testChannelId))
        .thenReturn(Optional.of(testChannelEntity));
    when(channelRepository.save(any(ChannelEntity.class)))
        .thenReturn(testChannelEntity);
    when(channelEntityMapper.toChannel(any(ChannelEntity.class)))
        .thenReturn(testChannelDto);

    //when
    ChannelDTO.Channel result = basicChannelService.updateChannel(command);

    //then
    assertNotNull(result);

  }

  @Test
  @DisplayName("채널 업데이트 실패 테스트 - 존재하지 않는 채널")
  void updateChannel_Fail_noSuchChannel() {

    //given
    String updatedName = "updated-name";
    String updatedDescription = "Updated Description";
    ChannelDTO.UpdateChannelCommand command = new ChannelDTO.UpdateChannelCommand(
        testChannelId, updatedName, ChannelType.PUBLIC, null, updatedDescription);

    when(channelRepository.existsById(testChannelId))
        .thenReturn(false);

    //when & then
    assertThrows(NoSuchChannelException.class, () -> {
      basicChannelService.updateChannel(command);
    });

  }

  @Test
  @DisplayName("채널 업데이트 실패 테스트 - 잘못된 채널 이름")
  void updateChannel_Fail_invalidChannelName() {

    //given
    String updatedName = "   ";
    String updatedDescription = "Updated Description";
    ChannelDTO.UpdateChannelCommand command = new ChannelDTO.UpdateChannelCommand(
        testChannelId, updatedName, ChannelType.PUBLIC, null, updatedDescription);

    when(channelRepository.existsById(testChannelId))
        .thenReturn(true);

    //when & then
    assertThrows(RuntimeException.class, () -> {
      basicChannelService.updateChannel(command);
    });

  }

  @Test
  @DisplayName("채널 삭제 성공 테스트")
  void deleteChannelById_Success() {

    //given
    when(channelRepository.existsById(testChannelId))
        .thenReturn(true);

    //when & then
    basicChannelService.deleteChannelById(testChannelId);

  }

  @Test
  @DisplayName("채널 삭제 실패 테스트 - 존재하지 않는 채널")
  void deleteChannelById_Fail_noSuchChannel() {

    //given
    when(channelRepository.existsById(testChannelId))
        .thenReturn(false);

    //when & then
    assertThrows(NoSuchChannelException.class, () -> {
      basicChannelService.deleteChannelById(testChannelId);
    });

  }

  @Test
  @DisplayName("채널 존재 여부 확인 테스트")
  void existChannelById() {

    //given
    when(channelRepository.existsById(testChannelId))
        .thenReturn(true);

    //when
    boolean exists = basicChannelService.existChannelById(testChannelId);

    //then
    assertTrue(exists);

  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 테스트")
  void findChannelsByUserId() {

    //given
    UUID userId = UUID.randomUUID();

    when(readStatusRepository.findByUserId(userId))
        .thenReturn(List.of());
    when(channelRepository.findByType(ChannelType.PUBLIC))
        .thenReturn(List.of(testChannelEntity));
    /*when(channelWithParticipants.addParticipantsToChannel(any(ChannelEntity.class)))
        .thenReturn(testChannelDto);*/

    //when
    List<ChannelDTO.Channel> result = basicChannelService.findChannelsByUserId(userId);

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());

  }

}