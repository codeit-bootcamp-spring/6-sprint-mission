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

  private String requestId;
  private UUID binaryContentId;
  private String errorMessage;

  @Builder(access = AccessLevel.PROTECTED)
  private FileUploadFailedEvent(String requestId, UUID binaryContentId, String errorMessage) {
    this.requestId = requestId;
    this.binaryContentId = binaryContentId;
    this.errorMessage = errorMessage;
  }

  public static FileUploadFailedEvent of(String requestId, UUID binaryContentId, String errorMessage) {
    return FileUploadFailedEvent.builder()
        .requestId(requestId)
        .binaryContentId(binaryContentId)
        .errorMessage(errorMessage)
        .build();
  }

}
