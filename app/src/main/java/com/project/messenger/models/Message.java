package com.project.messenger.models;

public class Message {
    private int id;
    private String content;
    private String senderName;
    private String senderEmail;
    private String createdAt;

    public Message() {
    }

    public Message(int id, String content, String senderName, String senderEmail, String createdAt) {
        this.id = id;
        this.content = content;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.createdAt = createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
