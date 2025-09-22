package com.sprint.mission.discodeit.dto.ChannelDto;

import com.sprint.mission.discodeit.entity.ChannelType;

public class ChannelCreatePublicDto {
    private ChannelType type;
    private String name;
    private String description;

    public ChannelCreatePublicDto() {}

    public ChannelCreatePublicDto(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
