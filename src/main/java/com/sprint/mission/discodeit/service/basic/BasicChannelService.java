package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Channel.*;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.enumtype.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelPrivateTypeUpdateNotPossibleException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        log.info("공개 채널 생성 요청 수신: channelName={} channelDescription={}",request.name(), request.description());
        Channel channel = new Channel(request.name(), request.description());
        channelRepository.save(channel);
        log.info("공개 채널 생성 완료: channelId={} ", channel.getId());
        return channelMapper.toDto(channel, new ArrayList<>(), null);
    }

    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {

        log.info("비공개 채널 생성 요청 수신");
        Channel channel = new Channel();

        List<User> users = request.participantIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(UserNotFoundException::new))
                .toList();

        List<ReadStatus> readStatuses = users.stream()
                .map(user -> new ReadStatus(channel, user))
                .toList();

        List<UserDto> userDtoList = userMapper.toDtoList(users);

        readStatusRepository.saveAll(readStatuses);

        channelRepository.save(channel);
        log.info("비공개 채널 생성 완료:  channelId={} ", channel.getId());

        return channelMapper.toDto(channel,userDtoList, null);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {

        log.info("채널 목록 조회 요청 수신: reqUserId={}", userId);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        //공개 채널부터 조회
        List<Channel> channels = channelRepository.findByType(ChannelType.PUBLIC);

        //유저로 읽음 싱테 조회
        //읽음 상태로 타켓 유저가 있는 비공개 채널 전채 채널 리스트에 추가
        readStatusRepository.findByUserId(user.getId()).stream()
                .filter(rs -> rs.getChannel().getType().equals(ChannelType.PRIVATE))
                .forEach(rs -> channels.add(rs.getChannel()));

        //디비에 검색을 위해 채널 아이디만 추출
        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        //타겟 유저가 포함된 비공개 체널에 있는 유저를 체널로 읽음 상태에서 조회
        Map<UUID, List<UserDto>> privateChannelDtoUser = readStatusRepository.findByChannelIdIn(channelIds).stream()
                .collect(Collectors.groupingBy(
                        readStatus -> readStatus.getChannel().getId(),
                        Collectors.mapping(readStatus -> userMapper.toDto(readStatus.getUser()), Collectors.toList())
                ));

        //타겟 유저가 포함된 모든 체널에 최근 메시지 시간 조회
        Map<UUID, Instant> lastMessageAt = messageRepository.findByChannelIdInOrderByCreatedAtDesc(channelIds).stream()
                .collect(Collectors.toMap(message -> message.getChannel().getId(), Message::getCreatedAt));


        //엔티티 디티오로 변환
        List<ChannelDto> findChannelDTOS = channelMapper.toDtoList(channels, privateChannelDtoUser, lastMessageAt);
        log.info("채널 목록 조회 완료");
        return List.copyOf(findChannelDTOS);
    }

    @Override
    public ChannelDto update(UUID channelID, PublicChannelUpdateRequest request) {
        log.info("채널 업데이트 요청 수신: channelId={} ", channelID);
        Channel channel = channelRepository.findById(channelID)
                .orElseThrow(ChannelNotFoundException::new);

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("비공개 채널 수정 발생:channelId={}, channelType={}", channel.getId(), channel.getType());
            throw new ChannelPrivateTypeUpdateNotPossibleException();
        }

        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId())
                .orElse(null);

        Instant lastMessageAt = lastMessage != null ? lastMessage.getCreatedAt() : null;

        channel.update(request.newName(), request.newDescription());
        channelRepository.save(channel);
        log.info("채널 업데이트 완료: channelId={} ", channel.getId());
        return channelMapper.toDto(channel, new ArrayList<>(), lastMessageAt);
    }

    @Override
    public void delete(UUID channelId) {
        log.info("채널 삭재 요청 수신:  channelId={}", channelId);

        Channel channel=channelRepository.findById(channelId).orElseThrow(ChannelNotFoundException::new);

        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannel().getId().equals(channel.getId()))
                .forEach(rsDelete -> readStatusRepository.deleteById(rsDelete.getId()));

        messageRepository.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channel.getId()))
                .forEach(mDelete -> messageRepository.deleteById(mDelete.getId()));

        channelRepository.deleteById(channelId);
        log.info("채널 삭재 완료");
    }
}
