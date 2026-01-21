package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)      // type 필드를 문자열로 저장
  @Column(name = "type", nullable = false)
  private ChannelType type;
  @Column
  private String name;
  @Column
  private String description;

  // 정적 팩토리 메서드
  public static Channel createPublic(String name, String description) {
    return new Channel(ChannelType.PUBLIC, name, description);
  }

  public static Channel createPrivate() {
    return new Channel(ChannelType.PRIVATE, "비공개 채널", "비공개 채널");
  }


  public void update(String newName, String newDescription) {
    boolean anyValueUpdated = false;
    if (!newName.equals(this.name)) {
      this.name = newName;
      anyValueUpdated = true;
    }
    if (!newDescription.equals(this.description)) {
      this.description = newDescription;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
