package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.model.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  UUID put(UUID binaryContentId, byte[] data);

  InputStream get(UUID binaryContentId) throws IOException;

  ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
