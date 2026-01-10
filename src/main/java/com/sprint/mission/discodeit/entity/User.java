package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
public class User extends BaseUpdatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "author")
    private List<Message> messages;

    // 참여중인 채널은 ReadStatus 엔터티로 확인 가능.
    // ReadStatus와 양방향매핑 불필요.

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private BinaryContent profileImage;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role = Role.USER;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder
    public User (String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public static User create(String email, String username, String encodedPassword) {
        return User.builder()
                .email(email)
                .username(username)
                .password(encodedPassword)
                .build();
    }

    public void update(String newEmail, String newUsername, String newPassword){
        if (newEmail != null) {
            this.email = newEmail;
        }
        if (newUsername != null) {
            this.username = newUsername;
        }
        if (newPassword != null) {
            this.password = newPassword;
        }
    }

    public void updateRole(Role newRole){
        this.role = newRole;
    }

}
