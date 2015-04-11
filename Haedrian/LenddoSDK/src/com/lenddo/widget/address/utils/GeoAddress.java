package com.lenddo.widget.address.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by joseph on 12/24/14.
 */
public class GeoAddress {
    private String featureName;
    private double latitude;
    private double longitude;
    private LatLng northEast;
    private LatLng southWest;

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setNorthEast(LatLng northEast) {
        this.northEast = northEast;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public void setSouthWest(LatLng southWest) {
        this.southWest = southWest;
    }

    public LatLng getSouthWest() {
        return southWest;
    }
}
