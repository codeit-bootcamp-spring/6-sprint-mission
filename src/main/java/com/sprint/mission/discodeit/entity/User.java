package com.sprint.mission.discodeit.entity;


import java.util.UUID;
import java.io.Serializable;
import java.util.Objects;


public class User implements Serializable  {
    private static final long serialVersionUID = 1L;

    private UUID Id;
    private String name;
    private long createAt;
    private long updateAt;


    public User(String name, long createAt, long updateAt ) {
        this.name = name;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public User(UUID Id, String name) {
    this.Id = Id;
    this.name = name;
    }


    public UUID getId() { return Id; }
    public String getName() { return name; }



    public void setId(UUID Id) { this.Id = Id; }
    public void setName(String name) { this.name = name; }



    @Override
    public String toString() {
        // UUID는 너무 길어 일부만 표시하여 가독성 높임
        return "User{id=" + (Id != null ? Id.toString().substring(0, 8) + "..." : "null")
                + ", name='" + name + "'}" ;
    }

    // equals와 hashCode는 객체 비교, ID를 기준으로 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Id, user.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}


