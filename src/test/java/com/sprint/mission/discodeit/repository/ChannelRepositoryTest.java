package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({AppConfig.class})
@DisplayName("ChannelRepository 테스트")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  private final String testChannelName = "testChannel";
  private final String testDescription = "Test channel description";
  private ChannelEntity testChannelEntity;

  @BeforeEach
  void setUp() {

    channelRepository.deleteAll();

    testChannelEntity = ChannelEntity.builder()
        .type(ChannelType.PUBLIC)
        .name(testChannelName)
        .description(testDescription)
        .build();

  }

  @Test
  @DisplayName("채널 생성 및 ID로 조회 - 성공")
  void saveAndFindById_Success() {

    //given
    ChannelEntity savedChannel = channelRepository.save(testChannelEntity);

    //when
    Optional<ChannelEntity> foundChannel = channelRepository.findById(savedChannel.getId());

    //then
    assertTrue(foundChannel.isPresent());
    assertEquals(testChannelName, foundChannel.get().getName());

  }

  @Test
  @DisplayName("채널 ID로 조회 - 실패: 존재하지 않는 ID")
  void findById_Fail_NoSuchChannel() {

    //given
    UUID nonExistentId = UUID.randomUUID();

    //when
    Optional<ChannelEntity> foundChannel = channelRepository.findById(nonExistentId);

    //then
    assertTrue(foundChannel.isEmpty());

  }

  @Test
  @DisplayName("채널 존재 여부 확인 - 성공")
  void existsById_Success() {

    //given
    ChannelEntity savedChannel = channelRepository.save(testChannelEntity);

    //when
    boolean exists = channelRepository.existsById(savedChannel.getId());

    //then
    assertTrue(exists);

  }

  @Test
  @DisplayName("채널 존재 여부 확인 - 실패: 존재하지 않는 ID")
  void existsById_Fail_NoSuchChannel() {

    //given
    UUID nonExistentId = UUID.randomUUID();

    //when
    boolean exists = channelRepository.existsById(nonExistentId);

    //then
    assertFalse(exists);

  }

  @Test
  @DisplayName("채널 삭제 - 성공")
  void deleteById_Success() {

    //given
    ChannelEntity savedChannel = channelRepository.save(testChannelEntity);

    //when
    channelRepository.deleteById(savedChannel.getId());

    //then
    assertFalse(channelRepository.existsById(savedChannel.getId()));

  }

  @Test
  @DisplayName("채널 삭제 - 실패: 존재하지 않는 ID")
  void deleteById_Fail_NoSuchChannel() {

    //given
    UUID nonExistentId = UUID.randomUUID();

    //when & then
    assertDoesNotThrow(() -> channelRepository.deleteById(nonExistentId));

  }

}