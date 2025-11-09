package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private EntityManager em;

  @BeforeEach
  void setUp() throws InterruptedException {
    Channel channel = new Channel(ChannelType.PUBLIC, "channel name", "channel description");
    channelRepository.save(channel);

    Message first = new Message("hello first", channel, null, null);
    Thread.sleep(1 / 1000); // 메시지 생성 시간 차이를 위해 잠시 대기
    Message second = new Message("hello second", channel, null, null);
    messageRepository.save(first);
    messageRepository.save(second);

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("채널 id로 메시지 조회 테스트")
  void findAllByChannel_Id() {
    // given
    UUID channelId = channelRepository.findAll().get(0).getId();

    // when
    List<Message> foundMessages = messageRepository.findAllByChannel_Id(channelId);

    // then
    assertThat(foundMessages).hasSize(2);
    assertThat(foundMessages.get(0).getChannel().getId()).isEqualTo(channelId);
    assertThat(foundMessages.get(1).getChannel().getId()).isEqualTo(channelId);
    assertThat(foundMessages.get(0).getContent()).isEqualTo("hello first");
    assertThat(foundMessages.get(1).getContent()).isEqualTo("hello second");
  }

  @Test
  @DisplayName("첫번째 메시지 생성시간 이전의 메시지 조회하는 커서 페이지네이션 테스트")
  void findAllByChannel_IdAndCreatedAtBefore() {
    // given
    UUID channelId = channelRepository.findAll().get(0).getId();
    Instant cursor = messageRepository.findAllByChannel_Id(channelId).get(0).getCreatedAt();
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Slice<Message> slice = messageRepository.findAllByChannel_IdAndCreatedAtLessThanEqual(channelId, cursor, pageable);

    // then
    assertThat(slice.getContent()).hasSize(1);
    assertThat(slice.getContent().get(0).getContent()).isEqualTo("hello first");
  }
}
