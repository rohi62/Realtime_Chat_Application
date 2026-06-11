package com.example.samefor.model;

public class UserModel {
    private String EMAIL;
    private String NAME;
    private String USERNAME;
    private String userId;
    private String fcmToken;

    public UserModel() {
    }

    public UserModel(String EMAIL, String NAME, String USERNAME, String userId) {
        this.EMAIL = EMAIL;
        this.NAME = NAME;
        this.USERNAME = USERNAME;
        this.userId = userId;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
