package com.haedrian.haedrian.Models;

/**
 * Created by Logan on 3/24/2015.
 */
public class UserModel {
    private int  walletId, creditScore;
    private String id, parseId, username, firstName, lastName, phoneNumber, email;
    private Long amount;

    public UserModel() {
        this.id = "";
        this.parseId = "";
        this.creditScore = 0;
        this.username = "";
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = "";
        this.email = "";
        this.amount = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParseId() { return parseId; }

    public void setParseId(String parseId) { this.parseId = parseId; }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAmount() { return this.amount; }

    public void setAmount(Long amount) { this.amount = amount; }
}