package com.sprint.mission.discodeit.enums;

public enum Role {

    ADMIN,
    CHANNEL_MANAGER,
    USER;

    public final String AUTHORITY_PREFIX = "ROLE_";
    public String getAuthority() {
        return AUTHORITY_PREFIX + this.name();
    }
}
