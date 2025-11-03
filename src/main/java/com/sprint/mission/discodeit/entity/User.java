package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseUpdatableEntity {

  @OneToOne(optional = true, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;
  // 양방향 관계는 순환참조 문제 발생 가능성 있음
  // dto 변환 시 한쪽은 상대 엔티티 제외
  @OneToOne(mappedBy = "user", optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus userStatus;
  //
  @Column(unique = true, nullable = false)
  private String username;
  @Column(unique = true, nullable = false)
  private String email;
  @Column(nullable = false)
  private String password;
  @Transient
  private Boolean online;

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.online = true;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
    if (userStatus != null && userStatus.getUser() != this) {
      userStatus.setUser(this);
    }
  }

  // 프론트엔드에서 유저이름과 이메일을 같은값으로 수정하면 null로 들어옴. null 체크
  // 비밀번호 같은 값은 그대로 값 들어옴
  public void update(String newUsername, String newEmail, String newPassword) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      anyValueUpdated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      anyValueUpdated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }

  }

  public void update(BinaryContent profile) {
    if (profile != null && profile != this.profile) {
      this.profile = profile;
    }
  }

  public void update(boolean online) {
    this.online = online;
  }
}
