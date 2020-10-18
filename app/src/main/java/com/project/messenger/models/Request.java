package com.project.messenger.models;

public class Request {
    private String id;
    private String from;
    private String to;
    private String message;
    private boolean status;
    private String fromUrl;

    public Request() {
    }

    public Request(String id, String from, String message, boolean status, String fromUrl) {
        this.id = id;
        this.from = from;
        this.message = message;
        this.status = status;
        this.fromUrl = fromUrl;
    }

    public void setFromUrl(String fromUrl) {
        this.fromUrl = fromUrl;
    }

    public String getFromUrl() {
        return fromUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}
