package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @Id
    @GeneratedValue
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // NULL 허용, 수정 가능.
    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    private Instant updatedAt;

    public boolean isOnline() {
        if (lastActiveAt == null) return false;
        Instant fiveMinuteAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        return lastActiveAt.isAfter(fiveMinuteAgo); // 5분전 시각이 마지막 접속시간보다 뒤이면 false 반환
    }
}