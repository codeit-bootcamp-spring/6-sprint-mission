package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.message.BinaryContentDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "memory")
public class InMemoryBinaryContentStorage implements BinaryContentStorage {

    private final Map<UUID, byte[]> storage = new HashMap<>();

    @Override
    public UUID put(UUID id, byte[] bytes) {
        storage.put(id, bytes);
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        byte[] bytes = storage.get(id);
        if (bytes == null) {
            throw new RuntimeException("File data not found for ID: " + id);
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        return ResponseEntity.ok().build();
    }
}