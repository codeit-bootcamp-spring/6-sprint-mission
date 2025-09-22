package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);

    List<BinaryContent> saveAll(List<BinaryContent> binaryContents);

    Optional<BinaryContent> findById(UUID id);

    Optional<BinaryContent> findByUserId(UUID userId);

    Optional<BinaryContent> findByMessageId(UUID messageId);

    void deleteById(UUID id);

    void deleteByUserId(UUID userId);

    void deleteByMessageId(UUID messageId);

}
