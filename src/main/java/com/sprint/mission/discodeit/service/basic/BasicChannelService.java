package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository, ReadStatusRepository readStatusRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel create(ChannelCreatePublicDto dto) {
        if (dto.getType() != ChannelType.PUBLIC) {
            throw new IllegalArgumentException("Invalid channel type for public channel creation.");
        }
        Channel channel = new Channel(dto.getType(), dto.getName(), dto.getDescription());
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(ChannelCreatePrivateDto dto) {
        dto.getParticipantIds().forEach(userId -> userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(userId + " not found.")));

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        dto.getParticipantIds().forEach(userId -> {
            ReadStatus readStatus = new ReadStatus(userId, createdChannel.getId());
            readStatusRepository.save(readStatus);
        });

        return createdChannel;
    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(channelId + " not found."));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        Stream<Channel> publicChannels = channelRepository.findAll().stream()
                .filter(c -> c.getType() == ChannelType.PUBLIC);

        Stream<Channel> privateChannels = readStatusRepository.findAllByUserId(userId).stream()
                .map(rs -> channelRepository.findById(rs.getChannelId())
                        .orElse(null))
                .filter(java.util.Objects::nonNull);

        return Stream.concat(publicChannels, privateChannels)
                .collect(Collectors.toList());
    }

    @Override
    public Channel update(UUID channelId, ChannelUpdateDto dto) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(channelId + " not found."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("Private channels cannot be updated.");
        }

        if (dto.getNewName() != null) {
            channel.setName(dto.getNewName());
        }
        if (dto.getNewDescription() != null) {
            channel.setDescription(dto.getNewDescription());
        }

        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(channelId + " not found.");
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }
}
