package com.app.dharaneesh.models;

public class UserModel {
    String id, username, email, role, expirationDate, creationDate,
            isEligibleForChat,  payment;

    public UserModel() {
    }

    public UserModel(String id, String username, String email, String role, String expirationDate, String creationDate, String isEligibleForChat, String payment) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expirationDate = expirationDate;
        this.creationDate = creationDate;
        this.isEligibleForChat = isEligibleForChat;
        this.payment = payment;
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

    public String getIsEligibleForChat() {
        return isEligibleForChat;
    }

    public void setIsEligibleForChat(String isEligibleForChat) {
        this.isEligibleForChat = isEligibleForChat;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
