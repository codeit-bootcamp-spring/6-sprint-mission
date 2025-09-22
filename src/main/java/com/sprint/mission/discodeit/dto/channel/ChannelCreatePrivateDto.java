package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public class ChannelCreatePrivateDto {
    private ChannelType type;
    private String name;
    private String description;
    private List<UUID> participantIds;

    public ChannelCreatePrivateDto() {}

    public ChannelCreatePrivateDto(ChannelType type, String name, String description, List<UUID> participantIds) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.participantIds = participantIds;

    }

    public List<UUID> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<UUID> participantIds) {
        this.participantIds = participantIds;
    }
}