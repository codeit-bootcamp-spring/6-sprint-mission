package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.enums.ChannelType;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    // 채널 생성 및 저장
    @Transactional
    public ChannelResponseDto create(PrivateChannelCreateRequestDto request) {

        Channel channel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();
        channelRepository.save(channel);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
                .map(user -> ReadStatus.builder()
                        .user(user)
                        .channel(channel)
                        .createdAt(channel.getCreatedAt())
                        .build()
                )
                .toList();
        readStatusRepository.saveAll(readStatuses);

        log.info("비공개 채널 생성이 완료되었습니다. id=" + channel.getId());
        return channelMapper.toDto(channel);
    }

    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public ChannelResponseDto create(PublicChannelCreateRequestDto request) {
        Channel channel = Channel.builder()
                .type(ChannelType.PUBLIC)
                .name(request.name())
                .description(request.description())
                .build();

        channelRepository.save(channel);
        log.info("공개 채널 생성이 완료되었습니다. id=" + channel.getId());
        return ChannelResponseDto.publicChannel( // public 채널은 participants가 없음.
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                null
        );
    }

    @Transactional(readOnly = true)
    public ChannelResponseDto findById(UUID id) {
        Channel channel = channelRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));

        Instant lastMessageSentAt = lastMessageSentAt(channel.getId());

        if (channel.getType() == ChannelType.PRIVATE) {
            return ChannelResponseDto.privateChannel(
                    channel.getId(),
                    getUserResponseDtos(channel),
                    lastMessageSentAt
                    );
        }

        else return ChannelResponseDto.publicChannel(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                lastMessageSentAt
                );
    }

    // 전체 PUBLIC, 참여중인 PRIVATE 채널
    @Transactional(readOnly = true)
    public List<ChannelResponseDto> findAllByUserId(UUID id) {

        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(id);
        Set<UUID> privateChannelIds = readStatuses.stream()
                .map(rs -> rs.getChannel().getId())
                .collect(Collectors.toSet());

        return channelRepository.findAllWithMessagesAndUsers().stream()
                .filter(channel -> channel.getType().equals(ChannelType.PUBLIC) || // 공개 채널
                                privateChannelIds.contains(channel.getId()) // 비공개 채널
                )
                .map(channel -> {
                    if (channel.getType() == ChannelType.PRIVATE) {

                        return ChannelResponseDto.privateChannel(
                                channel.getId(),
                                getUserResponseDtos(channel.getId()),
                                lastMessageSentAt(channel.getId())
                        );
                    } else {
                        return ChannelResponseDto.publicChannel(
                                channel.getId(),
                                channel.getName(),
                                channel.getDescription(),
                                lastMessageSentAt(channel.getId())
                        );
                    }
                })
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public ChannelResponseDto update(UUID id, PublicChannelUpdateRequestDto request) {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));

        if (channel.getType() == ChannelType.PRIVATE) throw new PrivateChannelUpdateException(id);

        channel.updatePublicChannel(request.newName(), request.newDescription());
        channelRepository.save(channel);
        log.info("채널 수정이 완료되었습니다. id=" + channel.getId());

        return ChannelResponseDto.publicChannel(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                lastMessageSentAt(channel.getId())
        );

    }

    // 채널 삭제
    @Transactional
    public void deleteById(UUID id, User user) {

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));

        if (channel.getType() == ChannelType.PUBLIC && user.getRole() == Role.USER) {
            throw new AccessDeniedException("권한이 없습니다."); // TODO 커스텀예외?
        }

        List<Message> messages = messageRepository.findByChannelId(id);
        if (messages != null) {
            messageRepository.deleteAll(messages);
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(id);
        if (readStatuses != null) {
            readStatuses.stream().
                    map(ReadStatus::getId).
                    forEach(readStatusRepository::deleteById);
        }

        channelRepository.delete(channel);
        log.info("채널 삭제가 완료되었습니다. id=" + id);
    }

    public Instant lastMessageSentAt(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));

        List<UUID> messageIds = channel.getMessages().stream()
                .map(Message::getId).toList();

        if (messageIds.isEmpty()) {
            return null;
        }

        List<Message> messages = messageRepository.findAllByIdIn(
                channel.getMessages().stream().map(Message::getId).toList()
        );

        return messages.stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    // 다건 조회
    public List<UserResponseDto> getUserResponseDtos(UUID channelId) {

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(channelId);

            return readStatuses.stream()
                    .map(ReadStatus::getUser)
                    .distinct()
                    .map(userMapper::toDto)
                    .toList();

        }

    // 단건 조회
    private List<UserResponseDto> getUserResponseDtos(Channel channel) {
        return channel.getReadStatuses().stream()
                .map(ReadStatus::getUser)
                .distinct()
                .map(userMapper::toDto)
                .toList();
    }
}
