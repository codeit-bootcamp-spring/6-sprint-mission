package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BinaryContent extends BaseEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName;
  @Column(nullable = false)
  private Long size;
  @Column(name = "content_type", nullable = false)
  private String contentType;
  @Setter
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BinaryContentStatus status;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.status = BinaryContentStatus.PROCESSING;
  }

  public void update(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  public void success() {
    this.status = BinaryContentStatus.SUCCESS;
  }

  public void fail() {
    this.status = BinaryContentStatus.FAILED;
  }
}
