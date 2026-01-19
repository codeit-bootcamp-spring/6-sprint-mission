package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public interface BinaryContentStorage {
    UUID put(UUID id, byte[] content)throws IOException;
    InputStream get(UUID id);
    ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
