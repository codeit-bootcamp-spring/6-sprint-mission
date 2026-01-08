package com.sprint.mission.discodeit.event.event;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FileUploadFailedEvent {

  private UUID binaryContentId;
  private String errorMessage;

  @Builder(access = AccessLevel.PROTECTED)
  private FileUploadFailedEvent(UUID binaryContentId, String errorMessage) {
    this.binaryContentId = binaryContentId;
    this.errorMessage = errorMessage;
  }

  public static FileUploadFailedEvent of(UUID binaryContentId, String errorMessage) {
    return FileUploadFailedEvent.builder()
        .binaryContentId(binaryContentId)
        .errorMessage(errorMessage)
        .build();
  }

}
