package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String userName;
    private final List<Channel> channels;

    public User(UUID id, String userName) {
        this.id = id;
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
        this.userName = userName;
        this.channels = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserName() { return userName;}
    public void setUserName(String userName) { this.userName = userName;}

    public List<Channel> getChannels() {
        return channels;
    }
    public void setChannels(Channel channel) {
        channels.add(channel);
    }

    @Override
    public String toString() {
        return "User Id: " + id + ", CreatedAt: " + createdAt + ", UpdatedAt: " + updatedAt;
    }

}
