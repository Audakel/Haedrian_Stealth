package com.lenddo.sdk.models;

/**
 * Created by joseph on 8/11/14.
 */
public class User {
    String id;
    String loginEmail;
    String firstName;
    private String gender;
    private int dayBirth;
    private int monthBirth;
    private int yearBirth;
    private String country;
    private String identity;
    private String locale;
    private String mobilePhone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    String lastName;
    String authToken;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setDayBirth(int dayBirth) {
        this.dayBirth = dayBirth;
    }

    public int getDayBirth() {
        return dayBirth;
    }

    public void setMonthBirth(int monthBirth) {
        this.monthBirth = monthBirth;
    }

    public int getMonthBirth() {
        return monthBirth;
    }

    public void setYearBirth(int yearBirth) {
        this.yearBirth = yearBirth;
    }

    public int getYearBirth() {
        return yearBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLocale() {
        return locale;
    }


    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }


    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
