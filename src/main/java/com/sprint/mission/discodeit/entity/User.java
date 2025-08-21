package com.sprint.mission.discodeit.entity;


import java.util.UUID;


public class User  {

    private UUID Id;
    private String name;
    private long createAt;
    private long updateAt;

    public User(UUID Id, String name, long createAt, long updateAt ) {
        this.Id = Id;
        this.name = name;
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
        return "{Id='" +"',name='"+ name + "',createAt='"+ createAt +
                "',updateAt='" + updateAt + "'}";

    }

}