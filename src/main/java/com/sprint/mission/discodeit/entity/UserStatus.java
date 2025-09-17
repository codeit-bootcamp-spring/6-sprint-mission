package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
public class UserStatus {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private boolean status;

    public UserStatus(UUID userId, Boolean status) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.status = false;
        this.createdAt = createdAt;
        this.updatedAt = Instant.now();
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void lastUpdateAt() {
        this.updatedAt = Instant.now();
    }

    public boolean isOnlineByUserId(UUID userId, long minutesToConsiderOnline) {
        if (this.updatedAt == null) {
            return false;  // 사용자 offline
        }
        //현재 시간에서 5분전에 시간이 thereshold 에저장
        Instant threshold = Instant.now().minusSeconds(minutesToConsiderOnline * 60);
        return this.updatedAt.isAfter(threshold);  // 사용자 5분 이내면 online,아니면 offline
    }

}