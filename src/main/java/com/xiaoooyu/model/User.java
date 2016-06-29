package com.xiaoooyu.model;

/**
 * Created by xiaoooyu on 5/30/16.
 */
public class User {

    public static final String ACCESS_TOKEN_KEY = "accessToken";
    public static final String USER_ELEM_KEY = "user";
    public static final String STRIKER_AUTH_KEY = "strikerAuth";

    private static final User SINGLETON = new User();
    public static User getInstance() {
        return SINGLETON;
    }

    private String accessToken;
    private String strikerAuth;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getStrikerAuth() {
        return strikerAuth;
    }

    public void setStrikerAuth(String strikerAuth) {
        this.strikerAuth = strikerAuth;
    }
}
