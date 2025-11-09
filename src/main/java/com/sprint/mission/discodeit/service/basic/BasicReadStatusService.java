package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        log.info("읽음 상태 생성 요청 수신: channelId={} userId={}",request.channelId(),request.userId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(ChannelNotFoundException::new);

        boolean isDuplication = readStatusRepository.findAll().stream()
                .anyMatch(rs ->
                        rs.getUser().getId().equals(user.getId())
                                && rs.getChannel().getId().equals(channel.getId()));
        if (isDuplication) {
            log.error("읽음 상태 중복 발생: userId={} readStatusId={}", user.getId(), user.getStatus().getId());
            throw new ReadStatusDuplicateException();
        }

        ReadStatus readStatus = readStatusRepository.save(new ReadStatus(channel, user));
        log.info("읽음 상태 생성 완료: readStatusId={}", readStatus.getId());
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.info("사용자 읽음 상태 목록 조회 요청 수신:  userId={}", userId);
        return readStatusRepository.findAll().stream()
                .filter(sr -> sr.getUser().getId().equals(userId))
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    public ReadStatusDto update(UUID readStatusId, Instant newLastReadAt) {
        log.info("읽음 상태 수정 요청 수신:  readStatusId={}", readStatusId);
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(ReadStatusNotFoundException::new);

        readStatus.update(newLastReadAt);
        readStatusRepository.save(readStatus);
        log.info("읽음 상태 수정 완료:  readStatusId={}", readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
