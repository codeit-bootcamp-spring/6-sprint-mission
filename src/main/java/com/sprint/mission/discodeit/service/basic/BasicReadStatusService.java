package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.exception.readstatus.AllReadyExistReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.NoSuchReadStatusException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.mapper.ReadStatusEntityMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusEntityMapper readStatusEntityMapper;

  @PreAuthorize("hasRole('USER')")
  @Transactional
  @Override
  public ReadStatusDTO.ReadStatus createReadStatus(ReadStatusDTO.CreateReadStatusCommand request) {

    if (!userRepository.existsById(request.userId())) {
      throw new NoSuchUserException();
    }

    if (!channelRepository.existsById(request.channelId())) {
      throw new NoSuchChannelException();
    }

    if (existReadStatusByUserIdAndChannelId(request.userId(), request.channelId())) {
      throw new AllReadyExistReadStatusException(Map.of(
          "userId", request.userId(),
          "channelId", request.channelId()
      ));
    }

    ChannelEntity channel = channelRepository.findById(request.channelId())
        .orElseThrow(NoSuchChannelException::new);

    ReadStatusEntity readStatusEntity = ReadStatusEntity.builder()
        .user(userRepository.findById(request.userId()).get())
        .channel(channelRepository.findById(request.channelId()).get())
        .lastReadAt(request.lastReadTimeAt())
        .notificationEnabled(channel.isPrivate())
        .build();

    return readStatusEntityMapper.toReadStatus(readStatusRepository.save(readStatusEntity));

  }

  @Override
  public boolean existReadStatusById(UUID id) {
    return readStatusRepository.existsById(id);
  }

  @Override
  public boolean existReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
    return readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
  }

  @Override
  public ReadStatusDTO.ReadStatus findReadStatusById(UUID id) {

    ReadStatusEntity readStatusEntity = readStatusRepository.findById(id)
        .orElseThrow(NoSuchReadStatusException::new);

    return readStatusEntityMapper.toReadStatus(readStatusEntity);
  }

  @Transactional(readOnly = true)
  @Override
  public ReadStatusDTO.ReadStatus findReadStatusByUserIdAndChannelId(UUID userId,
      UUID channelId) {

    if (!userRepository.existsById(userId)) {
      throw new NoSuchUserException();
    }

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchChannelException();
    }

    ReadStatusEntity readStatusEntity = readStatusRepository.findByUserIdAndChannelId(userId,
            channelId)
        .orElseThrow(NoSuchReadStatusException::new);

    return readStatusEntityMapper.toReadStatus(readStatusEntity);

  }

  @Override
  public List<ReadStatusDTO.ReadStatus> findAllReadStatusByUserId(UUID userId) {

    if (!userRepository.existsById(userId)) {
      throw new NoSuchUserException();
    }

    return readStatusRepository.findByUserId(userId)
        .stream()
        .map(readStatusEntityMapper::toReadStatus)
        .toList();
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDTO.ReadStatus> findAllReadStatusByChannelId(UUID channelId) {

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchChannelException();
    }

    return readStatusRepository.findByChannelId(channelId)
        .stream()
        .map(readStatusEntityMapper::toReadStatus)
        .toList();
  }

  @Override
  public List<ReadStatusDTO.ReadStatus> findAllReadStatus() {
    return readStatusRepository.findAll().stream()
        .map(readStatusEntityMapper::toReadStatus)
        .toList();
  }

  @PreAuthorize("hasRole('USER')")
  @Transactional
  @Override
  public ReadStatusDTO.ReadStatus updateReadStatus(ReadStatusDTO.UpdateReadStatusCommand request) {

    ReadStatusEntity readStatusEntity = readStatusRepository.findById(request.id())
        .orElseThrow(NoSuchReadStatusException::new);

    readStatusEntity.updateLastReadAt(request.lastReadAt());
    readStatusEntity.updateNotificationEnabled(request.newNotificationEnabled());

    return readStatusEntityMapper.toReadStatus(readStatusRepository.save(readStatusEntity));

  }

  @Transactional
  @Override
  public void deleteReadStatusById(UUID id) {

    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchReadStatusException();
    }

    readStatusRepository.deleteById(id);

  }

  @Transactional
  @Override
  public void deleteReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {

    if (!userRepository.existsById(userId)) {
      throw new NoSuchUserException();
    }

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchChannelException();
    }

    readStatusRepository.deleteByUserIdAndChannelId(userId, channelId);

  }

  @Transactional
  @Override
  public void deleteAllReadStatusByUserId(UUID userId) {

    if (!userRepository.existsById(userId)) {
      throw new NoSuchUserException();
    }

    readStatusRepository.deleteByUserId(userId);

  }

  @Transactional
  @Override
  public void deleteAllReadStatusByChannelId(UUID channelId) {

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchChannelException();
    }

    readStatusRepository.deleteByChannelId(channelId);

  }

  @Transactional
  @Override
  public void deleteAllReadStatusByIdIn(List<UUID> uuidList) {

    uuidList.forEach(uuid -> {
      if (!readStatusRepository.existsById(uuid)) {
        throw new NoSuchReadStatusException();
      }
    });

    readStatusRepository.deleteAllByIdIn(uuidList);

  }
}
