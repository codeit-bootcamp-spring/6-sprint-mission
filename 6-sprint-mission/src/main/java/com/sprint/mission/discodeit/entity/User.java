package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String state;
    private Common common;
    @Serial
    private static final long serialVersionUID = 1L;

    public User(String name) {
        this.name = name;
        this.state = "Online";
        this.common = new Common();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Common getCommon() {
        return common;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", userId=" + common.getId() +
                '}';
    }


}
