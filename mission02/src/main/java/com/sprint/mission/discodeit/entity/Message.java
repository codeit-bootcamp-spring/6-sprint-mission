package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String messageContext;
    private final String sender;
    private UUID channelId;

    public Message(UUID id, String messageContext, String sender, UUID channelId) {
        this.id = id;
        this.messageContext = messageContext;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.sender = sender;
        this.channelId = channelId;
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

    public void setMessageContext(String messageContext) {this.messageContext = messageContext;}

    public String getSender() {return sender;}

    public UUID getChannelId() {return channelId;}

    @Override
    public String toString() {
        return "메세지 내용 :" + messageContext + ", 작성시간:" + createdAt + ", 수정됨:" + updatedAt;
    }
}
