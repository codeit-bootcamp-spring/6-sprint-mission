package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelDataException;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.mapper.ChannelEntityMapper;
import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelEntityMapper channelEntityMapper;
  private final UserEntityMapper userEntityMapper;
  private final BasicChannelService.ChannelWithParticipants channelWithParticipants;

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDTO.Channel createChannel(ChannelDTO.CreatePublicChannelCommand request) {

    ChannelEntity channelEntity = ChannelEntity.builder()
        .type(ChannelType.PUBLIC)
        .name(request.name())
        .description(request.description())
        .build();

    log.debug("Creating channel with name {}, description {}", channelEntity.getName(),
        channelEntity.getDescription());

    return channelEntityMapper.toChannel(channelRepository.save(channelEntity));

  }

  @PreAuthorize("hasRole('USER')")
  @Transactional
  @Override
  public ChannelDTO.Channel createPrivateChannel(ChannelDTO.CreatePrivateChannelCommand request) {

    ChannelEntity channelEntity = ChannelEntity.builder()
        .type(request.type())
        .description(request.description())
        .build();

    ChannelDTO.Channel channel = channelEntityMapper.toChannel(
        channelRepository.save(channelEntity));

    List<ReadStatusEntity> readStatusEntityList = request.participants().stream()
        .map(userId -> ReadStatusEntity.builder()
            .user(userRepository.findById(userId)
                .orElseThrow(NoSuchChannelException::new))
            .channel(channelEntity)
            .lastReadAt(Instant.now())
            .notificationEnabled(true)
            .build())
        .toList();

    readStatusRepository.saveAll(readStatusEntityList);

    channel.addParticipants(readStatusEntityList.stream()
        .map(ReadStatusEntity::getUser)
        .filter(Objects::nonNull)
        .map(userEntityMapper::toUser)
        .toList());

    log.debug("Creating private channel with id {}, description {}, participants {}",
        channel.getId(), channel.getDescription(), channel.getParticipants().size());

    return channel;

  }

  @Override
  public boolean existChannelById(UUID id) {
    return channelRepository.existsById(id);
  }

  @Override
  public ChannelDTO.Channel findChannelById(UUID id) {

    ChannelEntity channelEntity = channelRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("No such channel with id {}", id);
          throw new NoSuchChannelException();
        });

    return channelWithParticipants.addParticipantsToChannel(channelEntity);

  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDTO.Channel> findChannelsByUserId(UUID userId) {

    List<ChannelDTO.Channel> channelList = new ArrayList<>(
        readStatusRepository.findByUserId(userId).stream()
            .map(ReadStatusEntity::getChannel)
            .map(channelWithParticipants::addParticipantsToChannel)
            .toList());

    channelList.addAll(channelRepository.findByType(ChannelType.PUBLIC).stream()
        .map(channelEntityMapper::toChannel)
        .toList());

    return channelList;

  }

  @Override
  public List<ChannelDTO.Channel> findAllChannels() {
    return channelRepository.findAll().stream()
        .map(channelWithParticipants::addParticipantsToChannel)
        .toList();
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDTO.Channel updateChannel(ChannelDTO.UpdateChannelCommand request) {

    if (!channelRepository.existsById(request.id())) {
      log.warn("No such channel with id {}", request.id());
      throw new NoSuchChannelException();
    }

    if (request.name().isBlank()) {
      log.warn("Invalid channel name for updateChannel with id {}", request.id());
      throw new InvalidChannelDataException(Map.of("name", "Channel name cannot be blank."));
    }

    /*if (request.type() == null) {
      log.warn("Missing channel type for updateChannel with id {}", request.id());
      throw new InvalidChannelDataException(Map.of("type", "Channel type is required."));
    }*/

    ChannelEntity updatedChannelEntity = channelRepository.findById(request.id())
        .orElseThrow(NoSuchChannelException::new);

    if (updatedChannelEntity.isPrivate()) {
      throw new InvalidChannelDataException(Map.of("private", "Channel is private."));
    }

    updatedChannelEntity.update(request.name(), request.description());

    log.debug("Updating channel with id {}, name {}, description {}",
        updatedChannelEntity.getId(), updatedChannelEntity.getName(),
        updatedChannelEntity.getDescription());

    return channelEntityMapper.toChannel(channelRepository.save(updatedChannelEntity));

  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public void deleteChannelById(UUID id) {

    if (!channelRepository.existsById(id)) {
      throw new NoSuchChannelException();
    }

    messageRepository.deleteByChannelId(id);
    readStatusRepository.deleteByChannelId(id);
    channelRepository.deleteById(id);

    log.debug("Deleted channel with id {}", id);

  }

  @RequiredArgsConstructor
  @Component
  static class ChannelWithParticipants {

    private final ChannelEntityMapper channelEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final ReadStatusRepository readStatusRepository;

    private ChannelDTO.Channel addParticipantsToChannel(ChannelEntity channelEntity) {

      ChannelDTO.Channel channel = channelEntityMapper.toChannel(channelEntity);

      if (channelEntity.getType() == ChannelType.PUBLIC) {
        return channel;
      }

      List<ReadStatusEntity> readStatusEntityList = readStatusRepository.findByChannelId(
          channelEntity.getId());

      channel.addParticipants(readStatusEntityList.stream()
          .map(ReadStatusEntity::getUser)
          .filter(Objects::nonNull)
          .map(userEntityMapper::toUser)
          .toList());

      return channel;

    }

  }


}
