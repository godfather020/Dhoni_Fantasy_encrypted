package com.app.dharaneesh.models;

public class mainchat {

    String senderid;
    String sendername;
    String groupname;
    String chattype;
    String message;
    long unreadcount;
    long timestamp;


    public mainchat() {
    }

    public mainchat(String senderid, String sendername, String groupname, String chattype,String message, long timestamp, long unreadcount) {
        this.senderid = senderid;
        this.sendername = sendername;
        this.groupname = groupname;
        this.chattype = chattype;
        this.timestamp = timestamp;
        this.message = message;
        this.unreadcount = unreadcount;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public long getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(long unreadcount) {
        this.unreadcount = unreadcount;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getChattype() {
        return chattype;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setChattype(String chattype) {
        this.chattype = chattype;
    }
}
