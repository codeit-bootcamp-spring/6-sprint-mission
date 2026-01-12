package com.sprint.mission.discodeit.storage.s3;

public class S3UploadFailedException extends RuntimeException {

  private final String key;

  public S3UploadFailedException(String key, Throwable cause) {
    super("S3 upload failed: " + key, cause);
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
