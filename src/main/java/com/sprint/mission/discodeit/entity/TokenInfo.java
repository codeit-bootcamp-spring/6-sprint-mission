package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 동시 로그인 제한
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
