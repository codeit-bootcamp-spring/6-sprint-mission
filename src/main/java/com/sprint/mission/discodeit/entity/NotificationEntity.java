package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notifications")
public class NotificationEntity extends BaseEntity {

  @Column(nullable = false)
  private UUID receiverId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Builder
  public NotificationEntity(UUID receiverId, String title, String content) {
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }

}
