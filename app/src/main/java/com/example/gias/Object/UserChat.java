package com.example.gias.Object;

import java.io.Serializable;

public class UserChat implements Serializable {
    private String phoneNumber;
    private String userName;
    private String linkAvatar;
    private String mes;
    private String object;
    private String index;

    public UserChat() {
    }

    public UserChat(String phoneNumber, String userName, String linkAvatar, String mes, String object, String index) {
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.linkAvatar = linkAvatar;
        this.mes = mes;
        this.index = index;
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}
