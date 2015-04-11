package com.lenddo.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.lenddo.data.listeners.OnLocationSubmittedListener;
import com.lenddo.data.models.AndroidContact;
import com.lenddo.data.models.CalendarEvent;
import com.lenddo.data.models.HistoryEntry;
import com.lenddo.data.models.SMSMessage;
import com.lenddo.data.models.UserCallLog;
import com.lenddo.data.receiver.LocationUpdateAlarmReceiver;
import com.lenddo.data.receiver.LocationUpdateSender;
import com.lenddo.data.receiver.WeeklyUpdateAlarmReceiver;
import com.lenddo.data.utils.DataHelper;
import com.lenddo.data.utils.Utils;
import com.lenddo.sdk.core.AndroidDataResponseListener;
import com.lenddo.sdk.core.LenddoClient;
import com.lenddo.sdk.listeners.UserListener;
import com.lenddo.sdk.models.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataManager implements AndroidDataResponseListener {

    public static final long ONE_DAY_IN_MILLIS = (3600000 * 24);
    private static final long ONE_WEEK_IN_MILLIS = (3600000 * 24 * 7);
    public static long ONE_HOUR_IN_MILLIS = 3600000;

    private static final String TAG = DataManager.class.getName();
    static DataManager manager;
    LenddoClient client;

    protected DataManager() {

    }

    public static DataManager getInstance() {
        if (manager == null) {
            Log.e(TAG, "no datamanager instance yet...");
        }
        return manager;
    }

    public static void setup(LenddoClient client) {
        DataManager.manager = new DataManager();
        DataManager.manager.setClient(client);
    }

    public void sendUsersSMS(List<SMSMessage> messagesChunk, String userId) {
        client.sendAndroidData(userId, DataHelper.getSMSJson(messagesChunk), this);
    }

    public void sendUsersCallLogs(List<UserCallLog> callLogs, String userId) {
        client.sendAndroidData(userId, DataHelper.getJsonFromUserCallLogs(callLogs), this);
    }

    public void sendBrowserHistory(ArrayList<HistoryEntry> list, String userId) {
        client.sendAndroidData(userId, DataHelper.getAndroidBrowserHistory(list), this);
    }

    public void sendUsersLocations(String locationJsonArray, String userId, OnLocationSubmittedListener onLocationSubmittedListener) {
        client.sendAndroidData(userId, DataHelper.getLocationArrayJson(locationJsonArray), this);

    }

    public void getMemberById(String userId, UserListener userListener) {
        client.getMemberById(userId, userListener);
    }

    public void setClient(LenddoClient client) {
        this.client = client;
    }

    public LenddoClient getClient() {
        return client;
    }

    public void sendCalendarEvents(String userId, ArrayList<CalendarEvent> events) {
        client.sendAndroidData(userId, DataHelper.getAndroidEventsJson(events), this);
    }

    public void sendUserContacts(String userId, List<AndroidContact> contacts) {
        client.sendAndroidData(userId, DataHelper.getAndroidContactsJson(contacts), this);
    }

    public void sendInstalledApps(String userId, List<PackageInfo> packages) {
        client.sendAndroidData(userId, DataHelper.getInstalledAppList(packages), this);
    }

    public static void configureAlarm(Context context) {
        Log.d(TAG, "Configure alarms");
        Utils.androidDataLog(context,TAG,"Setting up alarms");
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long startTime = System.currentTimeMillis();
        setHourlyAlarm(context, alarm, startTime);
        setDailyAlarm(context, alarm, startTime);
        setWeeklyAlarm(context, alarm, startTime);
    }

    public static void startAndroidData(Context context) {
        Intent intentSendUser = new Intent(context, SendUserService.class);
        context.startService(intentSendUser);
        configureAlarm(context);
    }


    public static void setWeeklyAlarm(Context context, AlarmManager alarm, long startTime) {
        Intent weeklyAlarmIntent = new Intent(context, WeeklyUpdateAlarmReceiver.class);
        PendingIntent weeklyAlarmPendingIntent = PendingIntent.getBroadcast(context, 0, weeklyAlarmIntent, 0);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime + ONE_WEEK_IN_MILLIS, ONE_WEEK_IN_MILLIS, weeklyAlarmPendingIntent);
        Utils.recordLastAlarmTriggered(context, WeeklyUpdateAlarmReceiver.class.getName(), System.currentTimeMillis());
        Utils.androidDataLog(context,TAG,"weekly alarm is set");
    }

    public static void setDailyAlarm(Context context, AlarmManager alarm, long startTime) {
        Intent dailyAlarmIntent = new Intent(context, LocationUpdateSender.class);
        PendingIntent dailyAlarmPendingIntent = PendingIntent.getBroadcast(context, 0, dailyAlarmIntent, 0);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime + ONE_DAY_IN_MILLIS, ONE_DAY_IN_MILLIS, dailyAlarmPendingIntent);
        Utils.recordLastAlarmTriggered(context, LocationUpdateSender.class.getName(), System.currentTimeMillis());
        Utils.androidDataLog(context,TAG,"daily alarm set.");
    }

    public static void setHourlyAlarm(Context context, AlarmManager alarm, long startTime) {
        Intent hourlyAlarmIntent = new Intent(context, LocationUpdateAlarmReceiver.class);
        PendingIntent hourlyAlarmPendingIntent = PendingIntent.getBroadcast(context, 0, hourlyAlarmIntent, 0);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime + ONE_HOUR_IN_MILLIS, ONE_HOUR_IN_MILLIS, hourlyAlarmPendingIntent);
        Utils.recordLastAlarmTriggered(context, LocationUpdateAlarmReceiver.class.getName(), System.currentTimeMillis());
        Utils.androidDataLog(context,TAG,"hourly alarm set.");
    }

    public static void checkAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Utils.androidDataLog(context,TAG, "checking if alarms have all lapsed.");
        long currentTime = System.currentTimeMillis();

        long lastHourlyAlarm = Utils.getLastAlarmTriggered(context, LocationUpdateAlarmReceiver.class.getName());
        Utils.androidDataLog(context,TAG,"hourly alarm last invocation at " + lastHourlyAlarm);
        if ( (currentTime - lastHourlyAlarm) > ONE_HOUR_IN_MILLIS * 2) {
            setHourlyAlarm(context, alarm, 0);
        }

        long lastDailyAlarm = Utils.getLastAlarmTriggered(context, LocationUpdateSender.class.getName());
        Utils.androidDataLog(context,TAG, "daily alarm last invocation at " + lastDailyAlarm);
        if ( (currentTime - lastDailyAlarm) > ONE_DAY_IN_MILLIS * 2) {
            setDailyAlarm(context, alarm, 0);
        }

        long lastWeeklyAlarm = Utils.getLastAlarmTriggered(context, WeeklyUpdateAlarmReceiver.class.getName());
        Utils.androidDataLog(context,TAG, "weekly alarm last invocation at " + lastWeeklyAlarm);
        if ( (currentTime - lastWeeklyAlarm) > ONE_WEEK_IN_MILLIS) {
            setWeeklyAlarm(context, alarm, 0);
        }
    }

    @Override
    public void onSuccess(JSONObject object) {

    }

    @Override
    public void onError(int statusCode, String rawResponse) {

    }


    public void sendUsersPhoneModel(String userId, String phoneNumberFromSim) {
        client.sendAndroidData(userId, DataHelper.getPhoneModelAndNumberJson(phoneNumberFromSim), this);
    }

    public void sendUsersLocation(Location location, String userId) {
        JSONObject data = DataHelper.getLocationJson(location);
    }
}
