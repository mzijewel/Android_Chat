package com.example.nayan.chatappupdated.model;

/**
 * Created by Dev on 1/15/2018.
 */

public class Message {
    private String content, userName;
    private long date;

    public Message() {
    }

    public Message(String content, String userName) {
        this.content = content;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
