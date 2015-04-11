package com.lenddo.sdk.models;

/**
 * Created by joseph on 8/20/14.
 */
public class AuthorizationStatus {
    private String verification;
    private String status;
    private String transId;
    private String userId;
    private String psid;

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public String getVerification() {
        return verification;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTransId() {
        return transId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setPsid(String psid) {
        this.psid = psid;
    }

    public String getPsid() {
        return psid;
    }
}
