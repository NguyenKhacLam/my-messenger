package com.project.messenger.models;

public class Message {
    private String id;
    private String content;
    private String senderName;
    private String senderEmail;
    private String senderImage;
    private String createdAt;

    public Message() {
    }

    public Message(String id, String content, String senderName, String senderEmail, String senderImage, String createdAt) {
        this.id = id;
        this.content = content;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderImage = senderImage;
        this.createdAt = createdAt;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setId(String id) {
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

    public String getId() {
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
