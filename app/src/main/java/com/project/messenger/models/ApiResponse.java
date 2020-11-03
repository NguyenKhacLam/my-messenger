package com.project.messenger.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("apiKey")
    private String apiKey;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("token")
    private String token;

    public ApiResponse() {
    }

    public ApiResponse(String apiKey, String sessionId, String token) {
        this.apiKey = apiKey;
        this.sessionId = sessionId;
        this.token = token;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getToken() {
        return token;
    }
}
