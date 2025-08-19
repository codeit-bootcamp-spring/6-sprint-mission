package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String channelName;
    private List<Message> messages;

    public Channel(UUID id, String channelName) {
        this.id = id;
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
        this.channelName = channelName;
        messages = new ArrayList<>();
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

    public String getChannelName() {return channelName;}
    public void setChannelName(String channelName) {this.channelName = channelName;}

    public List<Message> getMessages() {return messages;}
    public void setMessages(Message message) {messages.add(message);}

    @Override
    public String toString() {
        return "채널명: " + channelName + ", 생성일자: " + createdAt;
    }


}
