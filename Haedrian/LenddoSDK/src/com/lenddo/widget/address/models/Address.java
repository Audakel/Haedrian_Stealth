package com.lenddo.widget.address.models;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by joseph on 11/5/14.
 */
public class Address {
    private String barangay;
    private String city;
    private LatLng latLng;
    private String province;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String countryCode;

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setPostalCode(String zipCode) {
        this.postalCode = zipCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String toString() {
        ArrayList<String> addressComponentList = new ArrayList<String>();
        if (!TextUtils.isEmpty(getHouseNumber())) {
            addressComponentList.add(getHouseNumber());
        }

        if (!TextUtils.isEmpty(getStreet())) {
            addressComponentList.add(getStreet());
        }

        if (!TextUtils.isEmpty(getBarangay())) {
            addressComponentList.add(getBarangay());
        }

        if (!TextUtils.isEmpty(getCity())) {
            addressComponentList.add(getCity());
        }

        if (!TextUtils.isEmpty(getProvince())) {
            addressComponentList.add(getProvince());
        }

        if (!TextUtils.isEmpty(getCountryCode())) {
            Locale loc = new Locale("",getCountryCode());
            addressComponentList.add(loc.getDisplayCountry());
        }

        if (addressComponentList.size() > 0) {
            return StringUtils.join(addressComponentList, ", ");
        } else {
            return "";
        }
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
