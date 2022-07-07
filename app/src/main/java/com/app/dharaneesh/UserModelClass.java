package com.app.dharaneesh;

public class UserModelClass {
    String id, username, payment, email, role, expirationDate, creationDate, expireDays, chattype, groupname, topic,
    isEligibleForChat, unreadCount;

    public UserModelClass() {
    }

    public UserModelClass(String id, String username, String payment, String email, String role, String expirationDate,
                          String creationDate, String expireDays, String chattype, String groupname, String topic,
                          String isEligibleForChat) {
        this.id = id;
        this.username = username;
        this.payment = payment;
        this.email = email;
        this.expirationDate = expirationDate;
        this.creationDate = creationDate;
        this.expireDays = expireDays;
        this.chattype = chattype;
        this.groupname = groupname;
        this.topic = topic;
        this.isEligibleForChat = isEligibleForChat;
    }

    public String getIsEligibleForChat() {
        return isEligibleForChat;
    }

    public void setIsEligibleForChat(String isEligibleForChat) {
        this.isEligibleForChat = isEligibleForChat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(String expireDays) {
        this.expireDays = expireDays;
    }

    public String getChattype() {
        return chattype;
    }

    public void setChattype(String chattype) {
        this.chattype = chattype;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }
}