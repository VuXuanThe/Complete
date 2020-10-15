package com.example.gias.Object;

import java.io.Serializable;

public class Comment implements Serializable {
    private String uidLinkAvatar;
    private String name;
    private String time;
    private String comment;

    public Comment(String uidLinkAvatar, String name, String time, String comment) {
        this.uidLinkAvatar = uidLinkAvatar;
        this.name = name;
        this.time = time;
        this.comment = comment;
    }

    public Comment() {
    }

    public String getUidLinkAvatar() {
        return uidLinkAvatar;
    }

    public void setUidLinkAvatar(String uidLinkAvatar) {
        this.uidLinkAvatar = uidLinkAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
