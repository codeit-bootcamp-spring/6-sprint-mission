package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.User.UpdateUserDto;
import com.sprint.mission.discodeit.security.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;


@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseUpdatableEntity {

    @Column(unique = true, length = 50, nullable = false)
    private String username;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    @Column(nullable = false)
    Role role;


    public User(String username, String email, String password, BinaryContent profile, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
    }

    @Builder
    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }


    public void update(UpdateUserDto updateUserDTO) {
        boolean anyValueUpdated = false;

        if (updateUserDTO.username() != null
                && !updateUserDTO.username().isEmpty()
                && !updateUserDTO.username().equals(this.username)) {
            this.username = updateUserDTO.username();
            anyValueUpdated = true;
        }

        if (updateUserDTO.email() != null
                && !updateUserDTO.email().isEmpty()
                && !updateUserDTO.email().equals(this.email)) {
            this.email = updateUserDTO.email();
            anyValueUpdated = true;
        }

        if (updateUserDTO.password() != null
                && !updateUserDTO.password().isEmpty()
                && !updateUserDTO.password().equals(this.password)) {
            this.password = updateUserDTO.password();
            anyValueUpdated = true;
        }

        if (updateUserDTO.profile() != null && !updateUserDTO.profile().equals(this.profile)) {
            this.profile = updateUserDTO.profile();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAtNow();
        }
    }
    public void updateRole(Role role) {
        this.role = role;
    }
}
