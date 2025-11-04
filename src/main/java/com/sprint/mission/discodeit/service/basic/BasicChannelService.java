package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  public Channel createPublic(CreatePublicChannelRequest createPublicChannelRequest) {
    Channel channel = Channel.createPublic(createPublicChannelRequest.name(),
        createPublicChannelRequest.description());

    Channel saved = channelRepository.save(channel);

    log.info("공개 채널 생성: {}", saved.getId());
    log.debug("이름: {}, 설명: {}", saved.getName(), saved.getDescription());
    return saved;
  }

  public Channel createPrivate(CreatePrivateChannelRequest createPrivateChannelRequest) {
    Channel channel = Channel.createPrivate();
    Channel saved = channelRepository.save(channel);
    for (UUID userId : createPrivateChannelRequest.participantIds()) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> {
            log.warn("User Not Found. userId: {}", userId);
            return new UserNotFoundException();
          });
      readStatusRepository.save(
          new ReadStatus(user, saved, channel.getCreatedAt()));
    }

    log.info("비공개 채널 생성: {}", saved.getId());
    log.debug("참가자: {}", createPrivateChannelRequest.participantIds());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Channel find(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("Channel Not Found. channelId: {}", channelId);
          return new ChannelNotFoundException();
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Channel> findAllByUserId(UUID userId) {

    // 유저가 속한 채널 ID 리스트
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    // 유저가 속한 채널 리스트
    return channelRepository.findAll().stream().filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC) || mySubscribedChannelIds.contains(
                channel.getId()))
        .toList();
  }

  @Override
  public Channel update(UUID channelId, UpdateChannelRequest updateChannelRequest) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("Channel Not Found. channelId: {}", channelId);
          return new ChannelNotFoundException();
        });

    channel.update(updateChannelRequest.newName(), updateChannelRequest.newDescription());

    Channel updated = channelRepository.save(channel);

    log.info("채널 업데이트: {}", updated.getId());
    log.debug("이름: {}, 설명: {}", updated.getName(), updated.getDescription());
    return updated;
  }

  @Override
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      log.warn("Channel Not Found. channelId: {}", channelId);
      throw new ChannelNotFoundException();
    }
    // 같은 채널에서 나온 읽기상태들 삭제
    List<UUID> readStatusIds = readStatusRepository.findAllByChannel_Id(channelId)
        .stream()
        .map(BaseEntity::getId)
        .toList();
    readStatusRepository.deleteAllById(readStatusIds);
    // 같은 채널에서 나온 메시지들 삭제
    List<UUID> messageIds = messageRepository.findAllByChannel_Id(channelId)
        .stream()
        .map(BaseEntity::getId)
        .toList();
    messageRepository.deleteAllById(messageIds);
    // 채널 id로 삭제
    channelRepository.deleteById(channelId);

    log.info("채널 삭제: {}", channelId);
  }
}
