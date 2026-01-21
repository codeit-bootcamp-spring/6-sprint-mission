package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.MessageDTO;
import com.sprint.mission.discodeit.dto.PagingDTO;
import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.exception.message.NoSuchMessageException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.mapper.MessageEntityMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicMessageService 테스트")
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private MessageEntityMapper messageEntityMapper;

  @InjectMocks
  private BasicMessageService basicMessageService;

  private final UUID testMessageId = UUID.randomUUID();
  private final UUID testUserId = UUID.randomUUID();
  private final UUID testChannelId = UUID.randomUUID();
  private final String testContent = "Test message content";
  private MessageEntity testMessageEntity;
  private UserDTO.User testUserDto;
  private MessageDTO.Message testMessageDto;

  @BeforeEach
  void setUp() {

    UserEntity testUser = UserEntity.builder()
        .username("testuser")
        .build();

    ChannelEntity testChannel = ChannelEntity.builder()
        .name("test-channel")
        .build();

    testMessageEntity = MessageEntity.builder()
        .author(testUser)
        .channel(testChannel)
        .content(testContent)
        .build();

    testUserDto = UserDTO.User.builder()
        .id(testUserId)
        .username("testuser")
        //.createdAt(Instant.now())
        .build();

    testMessageDto = MessageDTO.Message.builder()
        .id(testMessageId)
        .author(testUserDto)
        .channelId(testChannelId)
        .content(testContent)
        .createdAt(Instant.now())
        .build();

  }

  @Test
  @DisplayName("메시지 생성 성공 테스트")
  void createMessage_Success() {

    //given
    byte[] testData = "test data".getBytes();

    MessageDTO.CreateMessageCommand command = new MessageDTO.CreateMessageCommand(
        testContent, testChannelId, testUserId, List.of()
    );

    when(userRepository.existsById(testUserId))
        .thenReturn(true);
    when(channelRepository.existsById(testChannelId))
        .thenReturn(true);
    when(userRepository.findById(testUserId))
        .thenReturn(Optional.of(testMessageEntity.getAuthor()));
    when(channelRepository.findById(testChannelId))
        .thenReturn(Optional.of(testMessageEntity.getChannel()));
    when(messageRepository.save(any(MessageEntity.class)))
        .thenReturn(testMessageEntity);
    when(messageEntityMapper.toMessage(any(MessageEntity.class)))
        .thenReturn(testMessageDto);

    //when
    MessageDTO.Message result = basicMessageService.createMessage(command);

    //then
    assertNotNull(result);
    assertEquals(testContent, result.getContent());

  }

  @Test
  @DisplayName("메시지 생성 실패 테스트 - 존재하지 않는 유저")
  void createMessage_Fail_noSuchUser() {

    //given
    MessageDTO.CreateMessageCommand command = new MessageDTO.CreateMessageCommand(
        testContent, testChannelId, testUserId, List.of()
    );

    when(userRepository.existsById(testUserId))
        .thenReturn(false);

    //when & then
    assertThrows(NoSuchUserException.class, () -> {
      basicMessageService.createMessage(command);
    });

  }

  @Test
  @DisplayName("메시지 생성 실패 테스트 - 존재하지 않는 채널")
  void createMessage_Fail_noSuchChannel() {

    //given
    MessageDTO.CreateMessageCommand command = new MessageDTO.CreateMessageCommand(
        testContent, testChannelId, testUserId, List.of()
    );

    when(userRepository.existsById(testUserId))
        .thenReturn(true);
    when(channelRepository.existsById(testChannelId))
        .thenReturn(false);

    //when & then
    assertThrows(NoSuchChannelException.class, () -> {
      basicMessageService.createMessage(command);
    });

  }

  @Test
  @DisplayName("메시지 ID로 조회 성공 테스트")
  void findMessageById_Success() {

    //given
    when(messageRepository.findById(testMessageId))
        .thenReturn(Optional.of(testMessageEntity));
    when(messageEntityMapper.toMessage(testMessageEntity))
        .thenReturn(testMessageDto);

    //when
    MessageDTO.Message result = basicMessageService.findMessageById(testMessageId);

    //then
    assertNotNull(result);
    assertEquals(testMessageId, result.getId());

  }

  @Test
  @DisplayName("메시지 ID로 조회 실패 테스트 - 존재하지 않는 메시지")
  void findMessageById_Fail_noSuchMessage() {

    //given
    when(messageRepository.findById(testMessageId))
        .thenReturn(Optional.empty());

    //when & then
    assertThrows(NoSuchMessageException.class, () -> {
      basicMessageService.findMessageById(testMessageId);
    });

  }

  @Test
  @DisplayName("사용자 ID로 메시지 조회 성공 테스트")
  void findMessagesByAuthorId_Success() {

    //given
    PagingDTO.OffsetRequest pageable = PagingDTO.OffsetRequest.of(0, 10);
    Page<MessageEntity> messagePage = new PageImpl<>(List.of(testMessageEntity));

    when(userRepository.existsById(testUserId)).thenReturn(true);
    when(messageRepository.findByAuthorId(testUserId, PageRequest.of(0, 10)))
        .thenReturn(messagePage);
    when(messageEntityMapper.toMessage(testMessageEntity))
        .thenReturn(testMessageDto);

    //when
    var result = basicMessageService.findMessagesByAuthorId(testUserId, pageable);

    //then
    assertNotNull(result);
    assertFalse(result.getContent().isEmpty());
    assertEquals(1, result.getContent().size());

  }

  @Test
  @DisplayName("채널 ID로 메시지 조회 성공 테스트")
  void findMessagesByChannelId_Success() {

    //given
    PagingDTO.OffsetRequest pageable = new PagingDTO.OffsetRequest(0, 10, "createdAt,DESC");
    Page<MessageEntity> messagePage = new PageImpl<>(List.of(testMessageEntity));

    when(channelRepository.existsById(testChannelId)).thenReturn(true);
    when(messageRepository.findByChannelId(eq(testChannelId), any(PageRequest.class)))
        .thenReturn(messagePage);
    when(messageEntityMapper.toMessage(testMessageEntity))
        .thenReturn(testMessageDto);

    //when
    var result = basicMessageService.findMessagesByChannelId(testChannelId, pageable);

    //then
    assertNotNull(result);
    assertFalse(result.getContent().isEmpty());

  }

  @Test
  @DisplayName("메시지 업데이트 성공 테스트")
  void updateMessage_Success() {

    //given
    String updatedContent = "Updated content";
    MessageDTO.UpdateMessageCommand command =
        new MessageDTO.UpdateMessageCommand(testMessageId, updatedContent);

    when(messageRepository.existsById(testMessageId))
        .thenReturn(true);
    when(messageRepository.findById(testMessageId))
        .thenReturn(Optional.of(testMessageEntity));
    when(messageRepository.save(any(MessageEntity.class)))
        .thenReturn(testMessageEntity);
    when(messageEntityMapper.toMessage(any(MessageEntity.class)))
        .thenReturn(testMessageDto);

    //when
    MessageDTO.Message result = basicMessageService.updateMessage(command);

    //then
    assertNotNull(result);

  }

  @Test
  @DisplayName("메시지 업데이트 실패 테스트 - 존재하지 않는 메시지")
  void updateMessage_Fail_noSuchMessage() {

    //given
    String updatedContent = "Updated content";
    MessageDTO.UpdateMessageCommand command =
        new MessageDTO.UpdateMessageCommand(testMessageId, updatedContent);

    when(messageRepository.existsById(testMessageId))
        .thenReturn(false);

    //when & then
    assertThrows(NoSuchMessageException.class, () -> {
      basicMessageService.updateMessage(command);
    });

  }

  @Test
  @DisplayName("메시지 삭제 성공 테스트")
  void deleteMessageById_Success() {

    //given
    when(messageRepository.findById(testMessageId))
        .thenReturn(Optional.of(testMessageEntity));

    //when & then
    basicMessageService.deleteMessageById(testMessageId);

  }

  @Test
  @DisplayName("메시지 삭제 실패 테스트 - 존재하지 않는 메시지")
  void deleteMessageById_Fail_noSuchMessage() {

    //given
    when(messageRepository.findById(testMessageId))
        .thenReturn(Optional.empty());

    //when & then
    assertThrows(NoSuchMessageException.class, () -> {
      basicMessageService.deleteMessageById(testMessageId);
    });

  }

}