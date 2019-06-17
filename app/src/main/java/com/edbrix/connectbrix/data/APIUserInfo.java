package com.edbrix.connectbrix.data;

public class APIUserInfo {
    public String userId;
    public String userZoomToken;
    public String userZoomAccessToken;

    public APIUserInfo(String userId, String userZoomToken, String userZoomAccessToken) {
        this.userId = userId;
        this.userZoomToken = userZoomToken;
        this.userZoomAccessToken = userZoomAccessToken;
    }
}
