package com.lenddo.data.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lenddo.data.DataManager;
import com.lenddo.data.listeners.OnLocationSubmittedListener;
import com.lenddo.data.utils.Utils;
import com.lenddo.sdk.listeners.UserListener;
import com.lenddo.sdk.models.User;

import org.json.JSONObject;

public class LocationUpdateSender extends BroadcastReceiver {

    private static final String TAG = LocationUpdateSender.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Utils.androidDataLog(context, TAG, "Daily location sender triggered.");
        Utils.recordLastAlarmTriggered(context, LocationUpdateSender.class.getName(), System.currentTimeMillis());
        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Utils.clearScheduleForLocationUpdate(context);
        if (!TextUtils.isEmpty(Utils.getUserId(context))) {
            if (Utils.isOnline(context)) {
                Utils.androidDataLog(context,TAG,"online. testing services.");
                DataManager.getInstance().getMemberById(Utils.getUserId(context), new UserListener() {

                    @Override
                    public void onSuccess(User user) {
                        Utils.androidDataLog(context,TAG,"services online. transmitting payload.");
                        transmitPayload(context);
                    }

                    @Override
                    public void onError(int statusCode, String rawResponse) {
                        Utils.androidDataLog(context,TAG,"services offline. reschedule.");
                        rescheduleAlarm(context, alarm);
                    }
                });

            } else {
                Utils.androidDataLog(context,TAG,"not online. reschedule.");
                rescheduleAlarm(context, alarm);
            }
        } else {
            Utils.androidDataLog(context, TAG, "no user logged in.");
        }
    }

    public void rescheduleAlarm(Context context, AlarmManager alarm) {
        Utils.androidDataLog(context, TAG, "marking for immediate upload.");
        Utils.scheduleForImmediateUpload(context, true);
    }

    public static void transmitPayload(Context context) {
        Utils.androidDataLog(context,TAG,"transmitting payload.");
        sendPendingLocationInfo(context);
        Utils.sendMessages(context);
        Utils.sendCallLogs(context);
        Utils.sendBrowserHistory(context);
        Utils.androidDataLog(context,TAG,"transmit payload done.");
    }

    public static void sendPendingLocationInfo(final Context context) {
        String locationJsonArray = Utils.getPendingLocationArray(context);
        if (locationJsonArray!=null) {
            Utils.androidDataLog(context,TAG,"location information not empty proceed with transmission.");
            DataManager.getInstance().sendUsersLocations(locationJsonArray, Utils.getUserId(context), new OnLocationSubmittedListener() {
                @Override
                public void onError() {
                    Log.d(TAG, "error with services. abort.");
                }

                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, "location upload successful");
                    Utils.clearPendingLocationArray(context);
                }
            });
        } else {
            Utils.androidDataLog(context,TAG,"location information not present. not sending anything.");
        }
    }
}
