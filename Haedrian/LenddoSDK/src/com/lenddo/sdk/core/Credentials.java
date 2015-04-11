package com.lenddo.sdk.core;

/**
 * Created by joseph on 8/11/14.
 */
public class Credentials {
    String userId;
    String secretKey;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
