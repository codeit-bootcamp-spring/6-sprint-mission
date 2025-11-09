package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentSave;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Page.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.file.FileInPutException;
import com.sprint.mission.discodeit.exception.file.FileOutPutException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto create(List<MultipartFile> multipartFiles, MessageCreateRequest messageCreateRequest) {
        log.info("메시지 생성 요청 수신: channelId={} userId={}",messageCreateRequest.channelId(),messageCreateRequest.authorId());
        User user = userRepository.findById(messageCreateRequest.authorId())
                .orElseThrow(UserNotFoundException::new);

        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
                .orElseThrow(ChannelNotFoundException::new);

        if (multipartFiles != null && !multipartFiles.get(0).isEmpty()) {
            List<BinaryContentSave> messageBinaryContents = getBinaryContents(multipartFiles);
            List<BinaryContent> binaryContents = new ArrayList<>();

            for (BinaryContentSave binaryContentSave : messageBinaryContents) {
                binaryContents.add(binaryContentSave.binaryContent());
                binaryContentRepository.save(binaryContentSave.binaryContent());
                binaryContentStorage.put(binaryContentSave.binaryContent().getId(), binaryContentSave.data());
            }

            Message message = new Message(user, channel, messageCreateRequest.content(), binaryContents);
            messageRepository.save(message);

            log.info("메시자 생성 완료: messageId={}", message.getId());
            return messageMapper.toDto(message);
        }

        Message message = new Message(user, channel, messageCreateRequest.content());
        messageRepository.save(message);
        log.info("메시자 생성 완료: messageId={}", message.getId());
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        log.info("메시지 목록 조회 요청 수신: channelId={}",channelId);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(ChannelNotFoundException::new);

        Slice<Message> slice;
        Instant NextCursor = null;
        if (cursor == null) {
            slice = messageRepository.findByChannelIdOrderByCreatedAtDesc(channel.getId(), pageable);
        }else{
            slice = messageRepository.findByCourseIdAndIdLessThanOrderByIdDesc(channel.getId(), cursor, pageable);
        }
        System.out.println(slice);

        Slice<MessageDto> messageDtoSlice = slice.map(messageMapper::toDto);

        System.out.println(messageDtoSlice);
        if (messageDtoSlice.hasContent()) {
            NextCursor = messageDtoSlice.getContent().get(messageDtoSlice.getContent().size()-1).createdAt();
        }

        return pageResponseMapper.fromSlice(messageDtoSlice,NextCursor);
    }

    @Override
    public MessageDto update(UUID messageId, String newContent) {
        log.info("메시지 업데이트 요청 수신:  messageId={}",messageId);
        Message messageUpdated = messageRepository.findById(messageId)
                .orElseThrow(MessageNotFoundException::new);

        messageUpdated.update(newContent);
        messageRepository.save(messageUpdated);
        log.info("메시지 업데이트 완료: messageId={}", messageId);
        return messageMapper.toDto(messageUpdated);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository
                .findById(id).orElseThrow(MessageNotFoundException::new);

        message.getAttachments()
                .forEach(attachment -> binaryContentRepository.deleteById(attachment.getId()));

        messageRepository.deleteById(id);
    }

    private List<BinaryContentSave> getBinaryContents(List<MultipartFile> multipartFiles) {
        log.info("메시지 첨부파일 저장 시작");
        List<BinaryContentSave> binaryContents = new ArrayList<>();
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                try {
                    BinaryContent binaryContent = new BinaryContent(
                            multipartFile.getOriginalFilename(),
                            multipartFile.getSize(),
                            multipartFile.getContentType()
                    );
                    binaryContents.add(new BinaryContentSave(binaryContent, multipartFile.getBytes()));
                } catch (IOException e) {
                    log.error("메시지 첨부 파일 저장 오류 발생: 파일 이름={}",multipartFile.getOriginalFilename());
                    throw new FileOutPutException();
                }
            }
        }
        log.info("메시지 첨부파일 저장 완료");
        return binaryContents;
    }
}
