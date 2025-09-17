package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.MessageDto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicReadStatusService(ReadStatusRepository readStatusRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.readStatusRepository = readStatusRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public ReadStatus create(ReadStatusCreateDto dto) {
        userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException(dto.userId() + " not found."));
        channelRepository.findById(dto.channelId())
                .orElseThrow(() -> new NoSuchElementException(dto.channelId() + " not found."));

        Optional<ReadStatus> existingStatus = readStatusRepository.findByUserIdAndChannelId(dto.userId(), dto.channelId());
        if (existingStatus.isPresent()) {
            System.out.println(dto.userId() + " and " + dto.channelId() + " already exists.");
            return existingStatus.get();
        }

        ReadStatus readStatus = new ReadStatus(dto.userId(), dto.channelId());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id + " not found."));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateDto dto) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id + " not found."));

        if (dto.lastReadAt() != null) {
            readStatus.setLastReadAt(dto.lastReadAt());
        } else {
            readStatus.lastReadTime();
        }

        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID userId, UUID channelId) {
        readStatusRepository.deleteById(userId);
        readStatusRepository.deleteAllByChannelId(channelId);
    }
}