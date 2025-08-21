package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class Channel {

    private UUID Id;
    private String name;
    private String channel;
    private long createAt;
    private long updateAt;

    public Channel(UUID Id, String name, String channel, long createAt,
                   long updateAt ) {
        this.Id = Id;
        this.name = name;
        this.channel = channel;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public void setId() {
        this.Id=Id;
    }
    public UUID getId() {
        return Id;
    }

    public void setName() {
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setChannel() {
        this.channel=channel;
    }
    public String getChannel() {
        return channel;
    }

    public void setCreateAt() {
        this.createAt=createAt;
    }
    public long getCreateAt() {
        return createAt;
    }
    public void setUpdateAt() {
        this.updateAt=updateAt;
    }
    public long getUpdateAt() {
        return updateAt;
    }
    @Override
    public String toString() {
        return "{channel='"+ channel + "',createAt='"+ createAt +
                "',updateAt='" + updateAt + "'}";

    }

}