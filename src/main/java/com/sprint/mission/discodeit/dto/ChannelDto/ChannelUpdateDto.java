package com.sprint.mission.discodeit.dto.ChannelDto;

public class ChannelUpdateDto {
    private String newName;
    private String newDescription;

    public ChannelUpdateDto() {}

    public ChannelUpdateDto(String newName, String newDescription) {
        this.newName = newName;
        this.newDescription = newDescription;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }
}
