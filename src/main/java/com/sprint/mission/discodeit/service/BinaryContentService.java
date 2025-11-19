package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentListNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    /**
     * @deprecated 이 메서드는 요구사항에 따라 만들어졌으나 아무 곳에서도 사용되지 않습니다.
     */
    @Deprecated(forRemoval = true)
    @Transactional
    public BinaryContentResponseDto create(BinaryContentCreateRequestDto request) {
        byte[] bytes = request.bytes();
        BinaryContent binaryContent = BinaryContent.builder()
                .fileName(request.fileName())
                .contentType(request.contentType())
                .size((long) bytes.length)
                .build();
        binaryContentRepository.save(binaryContent); // 메타데이터 DB에 저장
        binaryContentStorage.put(binaryContent.getId(), bytes); // 이미지 바이트 로컬에 저장
        log.info("파일이 업로드되었습니다. id=" + binaryContent.getId());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    public BinaryContentResponseDto findById(UUID id){
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids){
        return binaryContentRepository.findAllByIdIn(ids)
                .stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteById(UUID id){
        binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));
        binaryContentRepository.deleteById(id);
        log.info("BinaryContent 삭제가 완료되었습니다. id=" + id);
    }
}
