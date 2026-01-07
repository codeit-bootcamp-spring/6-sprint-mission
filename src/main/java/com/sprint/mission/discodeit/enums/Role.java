package com.sprint.mission.discodeit.enums;

public enum Role {
    ADMIN,
    CHANNEL_MANAGER,
    USER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
