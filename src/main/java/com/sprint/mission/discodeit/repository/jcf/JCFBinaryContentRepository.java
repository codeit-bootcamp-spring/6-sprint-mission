package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> storage = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        UUID id = binaryContent.getId();
        if (id == null) {
            id = UUID.randomUUID();
            binaryContent.setId(id);
        }
        storage.put(id, binaryContent);
        return binaryContent;
    }

    @Override
    public List<BinaryContent> saveAll(List<BinaryContent> binaryContents) {
        List<BinaryContent> savedContents = new ArrayList<>();
        for (BinaryContent content : binaryContents) {
            savedContents.add(save(content));
        }
        return savedContents;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        for (BinaryContent content : storage.values()) {
            if (content.getUserId() != null && content.getUserId().equals(userId)) {
                return Optional.of(content);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<BinaryContent> findByMessageId(UUID messageId) {
        for (BinaryContent content : storage.values()) {
            if (content.getMessageId() != null && content.getMessageId().equals(messageId)) {
                return Optional.of(content);
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UUID> idsToDelete = new ArrayList<>();
        for (BinaryContent content : storage.values()) {
            if (content.getUserId() != null && content.getUserId().equals(userId)) {
                idsToDelete.add(content.getId());
            }
        }
        for (UUID id : idsToDelete) {
            storage.remove(id);
        }
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        List<UUID> idsToDelete = new ArrayList<>();
        for (BinaryContent content : storage.values()) {
            if (content.getMessageId() != null && content.getMessageId().equals(messageId)) {
                idsToDelete.add(content.getId());
            }
        }
        for (UUID id : idsToDelete) {
            storage.remove(id);
        }
    }

}