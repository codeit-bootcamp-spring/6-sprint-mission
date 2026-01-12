package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
  @Column(unique = true, nullable = false)
  private String username;
  @Column(unique = true, nullable = false)
  private String email;
  @Column(nullable = false)
  private String password;
  @Enumerated(EnumType.STRING)
  private Role role;

  @Builder
  public User(String username, String email, Role role, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  // 프론트엔드에서 유저이름과 이메일을 같은값으로 수정하면 null로 들어옴. null 체크
  // 비밀번호 같은 값은 그대로 값 들어옴
  public boolean update(String newUsername, String newEmail, String newPassword) {
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

    return anyValueUpdated;
  }

  public void update(BinaryContent profile) {
    if (profile != null && profile != this.profile) {
      this.profile = profile;
    }
  }

  public boolean updateRole(Role role) {
    boolean anyValueUpdated = false;
    if (role != null && role != this.role) {
      this.role = role;
      anyValueUpdated = true;
    }
    return anyValueUpdated;
  }
}
