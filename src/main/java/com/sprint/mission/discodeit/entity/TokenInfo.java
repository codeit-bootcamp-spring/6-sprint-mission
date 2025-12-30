package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfo extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    public void rotate(String newAccess, String newRefresh) {
        this.accessToken = newAccess;
        this.refreshToken = newRefresh;
    }

}
