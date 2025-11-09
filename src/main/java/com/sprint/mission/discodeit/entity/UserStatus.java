package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "user_statuses")
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private Instant lastAccessAt;

    @Builder
    public UserStatus(User user) {
        this.user = user;
        this.lastAccessAt = Instant.now();
    }

    public void update(Instant lastAccessAt) {
        boolean anyValueUpdated = false;

        if (lastAccessAt != null && !lastAccessAt.equals(this.lastAccessAt)) {
            this.lastAccessAt = lastAccessAt;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAtNow();
        }
    }

    //접속 학인
    public boolean isConnecting(Instant newLastAccessAt) {
        long duration = newLastAccessAt.toEpochMilli() - this.lastAccessAt.toEpochMilli();
        if (duration <= 300000) {
            this.lastAccessAt = Instant.now();
            return true;
        }
        return false;
    }
}
