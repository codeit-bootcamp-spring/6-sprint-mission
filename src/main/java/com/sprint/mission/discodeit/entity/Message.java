package com.sprint.mission.discodeit.entity;


import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class Message {

    private UUID Id;
    private String name;
    private String message;
    private long createAt;
    private long updateAt;

    public Message(UUID Id, String name, String message, long createAt,
                   long updateAt ) {
        this.Id = Id;
        this.name = name;
        this.message = message;
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

    public void setMessage() {
        this.message=message;
    }
    public String getMessage() {
        return message;
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
        return "{message='"+ message + "',createAt='"+ createAt +
                "',updateAt='" + updateAt + "'}";

    }

}