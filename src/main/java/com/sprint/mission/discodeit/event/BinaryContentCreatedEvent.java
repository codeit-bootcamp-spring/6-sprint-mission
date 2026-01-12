package com.sprint.mission.discodeit.event;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] file
) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BinaryContentCreatedEvent that = (BinaryContentCreatedEvent) o;
    return Arrays.equals(file, that.file) && Objects.equals(binaryContentId,
        that.binaryContentId);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(binaryContentId);
    result = 31 * result + Arrays.hashCode(file);
    return result;
  }
}
