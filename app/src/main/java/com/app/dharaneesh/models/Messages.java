package com.app.dharaneesh.models;

public class Messages {
    String message, senderid;
    long timestamp;
    boolean isRead;


    public Messages() {
    }

    public Messages(String message, String senderid, long timestamp, boolean isRead) {
        this.message = message;
        this.senderid = senderid;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
