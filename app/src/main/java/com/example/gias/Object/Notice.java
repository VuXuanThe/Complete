package com.example.gias.Object;

import java.io.Serializable;

public class Notice implements Serializable {
    private String title;
    private String time;
    private String mes;

    public Notice(String title, String time, String mes) {
        this.title = title;
        this.time = time;
        this.mes = mes;
    }

    public Notice() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}
