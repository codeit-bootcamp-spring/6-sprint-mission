package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 반드시 새 트랜잭션으로 실행
    public void updateStatus(UUID id, BinaryContent.BinaryContentStatus status) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));
        binaryContent.updateStatus(status);
    }

    @Transactional
    public void deleteById(UUID id){
        binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));
        binaryContentRepository.deleteById(id);
        log.info("BinaryContent 삭제가 완료되었습니다. id=" + id);
    }
}
