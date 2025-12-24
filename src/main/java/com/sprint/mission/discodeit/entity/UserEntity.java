package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserEntity extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContentEntity profileId;

  @Column(nullable = false)
  private Role role = Role.USER;

  @Builder
  public UserEntity(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public void update(String nickname, String email, String password) {
    this.username = nickname;
    this.email = email;
    this.password = password;
  }

  public void updateProfile(BinaryContentEntity profileId) {
    this.profileId = profileId;
  }

  public void updateRole(Role role) {
    this.role = role;
  }


}
