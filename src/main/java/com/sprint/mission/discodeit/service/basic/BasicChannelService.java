package com.sprint.mission.discodeit.service.basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final CachedChannelService cachedChannelService;
    private final ObjectMapper objectMapper;
    private final SseService sseService;

    @Transactional
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(
            value = "channels",
            allEntries = true
    )
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.debug("채널 생성 시작: {}", request);
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        channelRepository.save(channel);
        log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    @CacheEvict(
            value = "channels",
            allEntries = true
    )
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.debug("채널 생성 시작: {}", request);
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
                .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
                .toList();
        readStatusRepository.saveAll(readStatuses);

        log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());

        ChannelDto channelDto = channelMapper.toDto(channel);
        sendSse("channels.created", channelDto);
        return channelDto;
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channelMapper::toDto)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        String result = cachedChannelService.findAllByUserId(userId);
        if (StringUtils.isEmpty(result))
            return List.of();

        try {
            return objectMapper.readValue(result, new TypeReference<List<ChannelDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ChannelNotFoundException();
        }
    }

    @Transactional
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(
            value = "channels",
            allEntries = true
    )
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("채널 수정 시작: id={}, request={}", channelId, request);
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw PrivateChannelUpdateException.forChannel(channelId);
        }
        channel.update(newName, newDescription);
        log.info("채널 수정 완료: id={}, name={}", channelId, channel.getName());

        ChannelDto channelDto = channelMapper.toDto(channel);

        sendSse("channels.updated", channelDto);

        return channelDto;
    }

    @Transactional
    @Override
    @CacheEvict(
            value = "channels",
            allEntries = true
    )
    public void delete(UUID channelId) {
        log.debug("채널 삭제 시작: id={}", channelId);
        if (!channelRepository.existsById(channelId)) {
            throw ChannelNotFoundException.withId(channelId);
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);

        sendSse("channels.deleted", find(channelId));

        channelRepository.deleteById(channelId);
        log.info("채널 삭제 완료: id={}", channelId);
    }

    private void sendSse(String name, ChannelDto channelDto) {
        sseService.send(channelDto.participants().stream().map(x -> x.id()).toList()
                , name
                , channelDto);
    }
}
