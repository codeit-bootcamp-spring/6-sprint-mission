package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;


  @Override
  public ReadStatus create(CreateReadStatusRequest createReadStatusRequest) {
    User user = userRepository.findById(createReadStatusRequest.userId())
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", createReadStatusRequest.userId());
          return new UserNotFoundException(Map.of("유저 고유 아이디", createReadStatusRequest.userId()));
        });
    Channel channel = channelRepository.findById(createReadStatusRequest.channelId())
        .orElseThrow(() -> {
          log.warn("Channel Not Found. channelId: {}", createReadStatusRequest.channelId(),
              MDC.get("requestId"));
          return new ChannelNotFoundException();
        });
    ReadStatus readStatus = new ReadStatus(user, channel, createReadStatusRequest.lastReadAt());
    return readStatusRepository.save(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("ReadStatus not found. readStatusId: {}", readStatusId);
          return new ReadStatusNotFoundException();
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUser_Id(userId);
  }

  @Override
  public ReadStatus update(UUID readStatusId, UpdateReadStatusRequest updateReadStatusRequest) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("ReadStatus not found. readStatusId: {}", readStatusId);
          return new ReadStatusNotFoundException();
        });
    readStatus.update(updateReadStatusRequest.newLastReadAt());
    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      log.warn("ReadStatus not found. readStatusId: {}", readStatusId);
      throw new ReadStatusNotFoundException();
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
