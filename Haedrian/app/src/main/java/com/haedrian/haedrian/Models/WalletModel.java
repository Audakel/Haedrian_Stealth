package com.haedrian.haedrian.Models;

/**
 * Created by Logan on 3/24/2015.
 */
public class WalletModel {
    private int id, userId;
    private String address, balance;

    public WalletModel() {
        this.id = 0;
        this.userId = 0;
        this.address = "";
        this.balance = "";
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
