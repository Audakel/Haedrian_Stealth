package com.lenddo.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;

import com.lenddo.data.listeners.OnLocationFoundListener;
import com.lenddo.data.utils.Utils;

public class LocationUpdateAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = LocationUpdateAlarmReceiver.class.getName();
    public static final int HOURS_TO_COLLECT = 24;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Utils.androidDataLog(context, TAG, "Location update alarm triggered.");
        Utils.recordLastAlarmTriggered(context, LocationUpdateAlarmReceiver.class.getName(), System.currentTimeMillis());
        if (!TextUtils.isEmpty(Utils.getUserId(context))) {
            Utils.androidDataLog(context, TAG, "user "+ Utils.getUserId(context) +" logged in. getting location");
            Utils.getLastKnownLocation(context, new OnLocationFoundListener() {
                @Override
                public void onLocationFound(Location location) {
                    int length = Utils.addToLocationArray(context, location);
                    Utils.androidDataLog(context, TAG, "Location obtained.");
                    if (length >= HOURS_TO_COLLECT) {
                        Utils.androidDataLog(context, TAG, "Number of hours collected exceeded. moving to send queue");
                        Utils.getAndClearLocationArray(length, context);
                    }
                }
            });
        } else {
            Utils.androidDataLog(context, TAG, "No user logged in.");
        }
    }
}
