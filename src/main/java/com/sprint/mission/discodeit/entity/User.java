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

    //Id생성자는 서비스에서 할당, 초기null 또는 자동생성
    public User(String name, long createAt, long updateAt ) {
        this.name = name;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public User(String name, String message) {
    }


    public UUID getId() { return Id; }
    public String getName() { return name; }



    public void setId(UUID Id) { this.Id = Id; }
    public void setName(String name) { this.name = name; }



    @Override
    public String toString() {

        return "User{id=" + Id + ", name='" + name + "'}";
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


