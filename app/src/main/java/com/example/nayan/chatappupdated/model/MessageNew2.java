package com.example.nayan.chatappupdated.model;

/**
 * Created by Dev on 1/31/2018.
 */

public class MessageNew2 {
    private String message, type,image;
    private long  time;
    private boolean seen;

    private String from;

    public MessageNew2(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public MessageNew2(String message,String image, String type, long time, boolean seen) {
        this.message = message;
        this.image = image;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public MessageNew2(){

    }
}
