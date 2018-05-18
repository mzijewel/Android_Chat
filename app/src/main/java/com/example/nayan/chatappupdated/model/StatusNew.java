package com.example.nayan.chatappupdated.model;

/**
 * Created by ASUS on 1/23/2018.
 */

public class StatusNew {
    public boolean isOnline;
    public long timestamp;

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
