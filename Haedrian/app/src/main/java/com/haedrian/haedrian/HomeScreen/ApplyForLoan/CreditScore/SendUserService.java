/*
package com.haedrian.haedrian.HomeScreen.ApplyForLoan.CreditScore;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.haedrian.haedrian.util.Utils;
import com.lenddo.data.CalendarEventsManager;
import com.lenddo.data.DataManager;
import com.lenddo.data.listeners.OnLocationFoundListener;

public class SendUserService extends IntentService {

    private Location location;
    private static final String TAG = SendUserService.class.getName();

    public SendUserService() {
        super("AndroidData");
    }

    private Location getLocation() {
        return location;
    }

    private void sendLocation() {
        if (location != null) {

            DataManager.getInstance().sendUsersLocation(getLocation(), Utils.getUserId(getApplicationContext()));
            Utils.setLocationSent(getApplicationContext(), true);
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Context context = getApplicationContext();
        Utils.androidDataLog(context,TAG,"First time setup. Sending data....");
        if (!TextUtils.isEmpty(Utils.getUserId(context))) {
            Utils.getLastKnownLocation(context , new OnLocationFoundListener() {
                @Override
                public void onLocationFound(Location location) {
                    SendUserService.this.location = location;
                    Log.d(TAG, "Intent service handled " + intent.getAction());
                    if (SendUserService.this.location != null) {
                        sendLocation();
                    } else {
                        Log.d(TAG,"Location information not available.");
                    }
                }
            });

            CalendarEventsManager.init(context);
            Utils.sendMessages(context);
            Utils.sendCallLogs(context);
            Utils.sendContacts(context);
            Utils.sendCalendarEvents(context);
            Utils.sendInstalledApps(context);
            Utils.sendBrowserHistory(context);
            Utils.sendPhoneModel(context);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
*/
