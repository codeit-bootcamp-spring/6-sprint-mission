package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SseService sseService;

    @Transactional
    @Override
    public BinaryContentDto create(UUID userId, BinaryContentCreateRequest request) {
        log.debug("바이너리 컨텐츠 생성 시작: fileName={}, size={}, contentType={}",
                request.fileName(), request.bytes().length, request.contentType());

        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();
        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType
        );
        binaryContentRepository.save(binaryContent);
        applicationEventPublisher.publishEvent(new BinaryContentCreatedEvent(userId, binaryContent.getId(), bytes));

        log.info("바이너리 컨텐츠 생성 완료: id={}, fileName={}, size={}",
                binaryContent.getId(), fileName, bytes.length);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("바이너리 컨텐츠 조회 시작: id={}", binaryContentId);
        BinaryContentDto dto = binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
        log.info("바이너리 컨텐츠 조회 완료: id={}, fileName={}",
                dto.id(), dto.fileName());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.debug("바이너리 컨텐츠 목록 조회 시작: ids={}", binaryContentIds);
        List<BinaryContentDto> dtos = binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
        log.info("바이너리 컨텐츠 목록 조회 완료: 조회된 항목 수={}", dtos.size());
        return dtos;
    }

    @Override
    @Transactional
    public BinaryContentDto updateStatus(UUID userID, UUID binaryContentId, BinaryContentStatus status) {
        log.debug("바이너리 컨텐츠 업데이트 시작: id={}, new Status={}", binaryContentId, status);

        BinaryContent content = binaryContentRepository.findById(binaryContentId).orElseThrow(
                () -> BinaryContentNotFoundException.withId(binaryContentId));

        content.updateStatus(status);
        binaryContentRepository.save(content);
        log.info("바이너리 컨텐츠 업데이트 완료");

        BinaryContentDto dto = binaryContentMapper.toDto(content);

        sseService.send(List.of(userID), "binaryContents.updated", dto);

        return dto;
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.debug("바이너리 컨텐츠 삭제 시작: id={}", binaryContentId);
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw BinaryContentNotFoundException.withId(binaryContentId);
        }
        binaryContentRepository.deleteById(binaryContentId);
        log.info("바이너리 컨텐츠 삭제 완료: id={}", binaryContentId);
    }
}
