package com.sprint.mission.discodeit.service.repository.file;

import java.util.UUID;

public class UserChannelRelationship {
    private final UUID userId;
    private final UUID channelId;

    public UserChannelRelationship(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
    }

    public UUID getUserId() {
        return userId;
    }
    public UUID getChannelId() {
        return channelId;
    }

}
