package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Page<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);

  // 특정 채널의 메시지들을 cursor를 받아 시간 기준으로 이전 메시지들을 페이징 처리하여 조회
  Slice<Message> findAllByChannel_IdAndCreatedAtLessThanEqual(
      UUID channelId,
      Instant cursor, Pageable pageable);

  List<Message> findAllByChannel_Id(UUID channelId);
}
