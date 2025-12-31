package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class})
@DisplayName("MessageRepository 테스트")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  private final String testContent = "Test message content";
  private MessageEntity testMessageEntity;
  private UserEntity testUserEntity;
  private ChannelEntity testChannelEntity;

  @BeforeEach
  void setUp() {

    messageRepository.deleteAll();
    userRepository.deleteAll();
    channelRepository.deleteAll();

    testUserEntity = UserEntity.builder()
        .username("testuser")
        .email("test@example.com")
        .password("hashedPassword123@")
        .build();
    userRepository.save(testUserEntity);

    testChannelEntity = ChannelEntity.builder()
        .type(ChannelType.PUBLIC)
        .name("testChannel")
        .description("Test channel")
        .build();
    channelRepository.save(testChannelEntity);

    testMessageEntity = MessageEntity.builder()
        .content(testContent)
        .author(testUserEntity)
        .channel(testChannelEntity)
        .build();

  }

  @Test
  @DisplayName("메시지 생성 및 ID로 조회 - 성공")
  void saveAndFindById_Success() {

    //given
    MessageEntity savedMessage = messageRepository.save(testMessageEntity);

    //when
    var foundMessage = messageRepository.findById(savedMessage.getId());

    //then
    assertTrue(foundMessage.isPresent());
    assertEquals(testUserEntity.getId(), foundMessage.get().getAuthor().getId());
    assertEquals(testChannelEntity.getId(), foundMessage.get().getChannel().getId());

  }

  @Test
  @DisplayName("메시지 ID로 조회 - 실패: 존재하지 않는 ID")
  void findById_Fail_NoSuchMessage() {

    //given
    UUID nonExistentId = UUID.randomUUID();

    //when
    var foundMessage = messageRepository.findById(nonExistentId);

    //then
    assertTrue(foundMessage.isEmpty());

  }

  @Test
  @DisplayName("채널 ID로 메시지 조회 - 성공")
  void findByChannelId_Success() {

    //given
    messageRepository.save(testMessageEntity);
    PageRequest pageable = PageRequest.of(0, 10);

    //when
    Page<MessageEntity> messages = messageRepository.findByChannelId(testChannelEntity.getId(), pageable);

    //then
    assertFalse(messages.isEmpty());
    assertEquals(1, messages.getTotalElements());
    assertEquals(testContent, messages.getContent().get(0).getContent());

  }

  @Test
  @DisplayName("채널 ID와 생성 시간으로 메시지 조회 - 성공")
  void findByChannelIdAndCreatedAt_Success() {

    //given
    MessageEntity savedMessage = messageRepository.save(testMessageEntity);
    Instant oneHourAgo = Instant.now().minusSeconds(3600);
    int pageSize = 10;

    //when
    Slice<MessageEntity> messages = messageRepository.findByChannelIdAndCreatedAt(
        testChannelEntity.getId(),
        oneHourAgo,
        pageSize
    );

    //then
    assertFalse(messages.isEmpty());
    assertEquals(testContent, messages.getContent().get(0).getContent());

  }

  @Test
  @DisplayName("모든 메시지 조회 - 성공")
  void findAll_Success() {

    //given
    messageRepository.save(testMessageEntity);
    PageRequest pageable = PageRequest.of(0, 10);

    //when
    Page<MessageEntity> messages = messageRepository.findAll(pageable);

    //then
    assertFalse(messages.isEmpty());
    assertEquals(1, messages.getTotalElements());
    assertEquals(testContent, messages.getContent().get(0).getContent());

  }

  @Test
  @DisplayName("모든 메시지 조회 - 결과 없음")
  void findAll_NoResults() {

    //given
    PageRequest pageable = PageRequest.of(0, 10);

    //when
    Page<MessageEntity> messages = messageRepository.findAll(pageable);

    //then
    assertTrue(messages.isEmpty());
    assertEquals(0, messages.getTotalElements());

  }

  @Test
  @DisplayName("메시지 삭제 - 성공")
  void deleteById_Success() {

    //given
    MessageEntity savedMessage = messageRepository.save(testMessageEntity);

    //when
    messageRepository.deleteById(savedMessage.getId());

    //then
    assertFalse(messageRepository.existsById(savedMessage.getId()));

  }

  @Test
  @DisplayName("채널 ID로 메시지 삭제 - 성공")
  void deleteByChannelId_Success() {

    //given
    messageRepository.save(testMessageEntity);

    //when
    messageRepository.deleteByChannelId(testChannelEntity.getId());

    //then
    Page<MessageEntity> messages = messageRepository.findByChannelId(
        testChannelEntity.getId(),
        PageRequest.of(0, 10)
    );
    assertTrue(messages.isEmpty());

  }

}