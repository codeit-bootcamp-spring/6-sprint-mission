package com.sprint.mission.discodeit.security;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    CHANNEL_MANAGER("ROLE_CHANNEL_MANAGER"),
    ADMIN("ROLE_ADMIN");

    private final String key;

    Role(String key) {
        this.key = key;
    }
}