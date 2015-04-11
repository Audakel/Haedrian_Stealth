package com.lenddo.data.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lenddo.data.BuildConfig;
import com.lenddo.data.CalendarEventsManager;
import com.lenddo.data.Constants;
import com.lenddo.data.DataManager;
import com.lenddo.data.listeners.OnLocationFoundListener;
import com.lenddo.data.models.AndroidContact;
import com.lenddo.data.models.CalendarEvent;
import com.lenddo.data.models.HistoryEntry;
import com.lenddo.data.models.SMSMessage;
import com.lenddo.data.models.UserCallLog;
import com.lenddo.data.receiver.LocationUpdateSender;
import com.lenddo.data.receiver.WeeklyUpdateAlarmReceiver;
import com.lenddo.sdk.listeners.UserListener;
import com.lenddo.sdk.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss 'GMT' yyyy";
    private static final String NEXT_ALARM_SCHEDULE = "next_alarm_schedule";
    private static final String DAILY_LOCATION_ARRAY = "location_array";
    private static final String DAILY_LOCATION_ARRAY_PENDING = "pending_location_array";
    private static final String SCHEDULE_FOR_IMMEDIATE_UPLOAD = "immediate_upload";
    private static final String LOCATION_UPDATE_SCHEDULE = "location_update_alarm_schedule";
    private static final String SMS_LAST_ID = "last_queried_sms_id";
    private static final String CALLLOG_LAST_ID = "call_log_last_queried_id";
    private static final String SCHEDULE_CONTACTS_FOR_IMMEDIATE_UPLOAD = "contacts_immediate_upload";
    private static final String BROWSER_HISTORY_LAST_ID = "last_queried_browser_hisotry_id";
    private static final String TAG = Utils.class.getName();
    private static final String LOCATION_SENT = "location_sent";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void clearScheduleForLocationUpdate(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().remove(LOCATION_UPDATE_SCHEDULE).commit();
    }

    public static long getLastSmsIdQueried(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getLong(SMS_LAST_ID, 0);
    }

    public static void saveLastIdQueried(Context context, long lastId) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putLong(SMS_LAST_ID, lastId).commit();
    }

    public static final SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_MULTI_PROCESS);
    }

    public static long getLastAlarmTriggered(Context context, String scheduleName) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getLong(scheduleName, 0);
    }

    public static void recordLastAlarmTriggered(Context context, String scheduleName, long millis) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(scheduleName, millis);
        editor.commit();
    }

    public static void scheduleForImmediateUpload(Context context, boolean b) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putBoolean(SCHEDULE_FOR_IMMEDIATE_UPLOAD, b).commit();
    }

    public static final void setUserId(Context context, String userId) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putString(Constants.USER_ID, userId).commit();
    }

    public static final String getUserId(Context context) {
        return getUserSharedPreferences(context).getString(Constants.USER_ID, "");
    }

    public static void sendInstalledApps(Context context) {
        Log.d(TAG, "get installed apps");
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        DataManager.getInstance().sendInstalledApps(Utils.getUserId(context), packages);
    }

    public static void saveLastCallIdQueried(Context context, long lastId) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putLong(CALLLOG_LAST_ID, lastId).commit();
    }

    public static long getLastCallIdQueried(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getLong(CALLLOG_LAST_ID, 0);
    }

    public static final String getPhoneNumberFromSim(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }


    private static void setIsPhoneModelSent(Context context, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.PHONE_MODEL_SENT, b).commit();
    }

    public static void sendPhoneModel(Context context) {
        if (!isPhoneModelSent(context)) {
            setIsPhoneModelSent(context, true);
            DataManager.getInstance().sendUsersPhoneModel(Utils.getUserId(context.getApplicationContext()), Utils.getPhoneNumberFromSim(context));
        }
    }

    private static boolean isPhoneModelSent(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PHONE_MODEL_SENT, false);
    }



    public static <T> List<List<T>> partition(List<T> sourceList, int partitionSize) {
        List<List<T>> partitions = new LinkedList<List<T>>();
        for (int i = 0; i < sourceList.size(); i += partitionSize) {
            partitions.add(sourceList.subList(i,
                    i + Math.min(partitionSize, sourceList.size() - i)));
        }
        return partitions;
    }

    public static boolean hasPermission(Context context, String permissionStr) {
        String permission =permissionStr;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void sendMessages(Context context) {
        if (hasPermission(context, "android.permission.READ_SMS")) {
            List<SMSMessage> messages = SMSManager.INSTANCE.getSMSMessages(context.getApplicationContext());
            Utils.androidDataLog(context, TAG, "sending SMS messages " + messages.size());
            if (messages.size() > 0) {
                List<List<SMSMessage>> partitions = partition(messages, 20);
                for (List<SMSMessage> messagesChunk : partitions) {
                    DataManager.getInstance().sendUsersSMS(messagesChunk, getUserId(context.getApplicationContext()));
                    setSMSSent(context.getApplicationContext(), true);
                }
            }
        }
    }


    private static void saveLastBrowserHistoryQueried(Context context, long last_id) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putLong(BROWSER_HISTORY_LAST_ID, last_id).commit();
    }

    private static long getLastBrowserHistoryQueried(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getLong(BROWSER_HISTORY_LAST_ID, 0);
    }

    public static String getPendingLocationArray(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getString(DAILY_LOCATION_ARRAY_PENDING, null);
    }

    public static void clearPendingLocationArray(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putString(DAILY_LOCATION_ARRAY_PENDING, null).commit();
    }


    public static boolean isContactsScheduledForImmediateUpload(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getBoolean(SCHEDULE_CONTACTS_FOR_IMMEDIATE_UPLOAD, false);
    }


    public static void sendBrowserHistory(Context context) {
        if (hasPermission(context, "com.android.browser.permission.READ_HISTORY_BOOKMARKS")) {
            String title = "";
            String url = "";

            ArrayList<HistoryEntry> list = new ArrayList<HistoryEntry>();
            long last_id = Utils.getLastBrowserHistoryQueried(context);
            String[] proj = new String[]{Browser.BookmarkColumns._ID, Browser.BookmarkColumns.DATE,
                    Browser.BookmarkColumns.TITLE,
                    Browser.BookmarkColumns.URL};

            Cursor mCur = null;
            try {
                if (last_id != 0) {
                    String sel = Browser.BookmarkColumns.BOOKMARK + " = 0 AND _id > " + last_id; // 0 = history,
                    // 1 = bookmark
                    mCur = context.getContentResolver().query(Browser.BOOKMARKS_URI, proj,
                            sel, null, "_id ASC");
                } else {
                    String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history,
                    // 1 = bookmark
                    mCur = context.getContentResolver().query(Browser.BOOKMARKS_URI, proj,
                            sel, null, null);
                }


                if (mCur.getCount() > 0) {
                    while (mCur.moveToNext()) {
                        HistoryEntry entry = new HistoryEntry();
                        int id = mCur.getInt(mCur.getColumnIndex(Browser.BookmarkColumns._ID));

                        if (id > last_id) {
                            last_id = id;
                        }

                        title = mCur.getString(mCur
                                .getColumnIndex(Browser.BookmarkColumns.TITLE));
                        url = mCur.getString(mCur
                                .getColumnIndex(Browser.BookmarkColumns.URL));
                        String date = mCur.getString(mCur
                                .getColumnIndex(Browser.BookmarkColumns.DATE));
                        // Do something with title and url
                        entry.setId(id);
                        entry.setTitle(title);
                        entry.setDate(date);
                        entry.setUrl(url);
                        list.add(entry);
                    }
                }
            } finally {
                if (mCur != null) {
                    mCur.close();
                }
            }
            Utils.saveLastBrowserHistoryQueried(context, last_id);
            if (list.size() > 0) {
                DataManager.getInstance().sendBrowserHistory(list, Utils.getUserId(context.getApplicationContext()));
            }
        }

    }

    public static void sendCallLogs(Context context) {
        if (hasPermission(context, "android.permission.READ_CALL_LOG")) {
            List<UserCallLog> result = SMSManager.INSTANCE.getCallLogs(context.getApplicationContext());
            Utils.androidDataLog(context, TAG, "sending call logs size " + result.size());
            if (result.size() > 0) {
                DataManager.getInstance().sendUsersCallLogs(result, Utils.getUserId(context.getApplicationContext()));
            }
        }
    }

    public static void sendCalendarEvents(Context context) {
        CalendarEventsManager manager = CalendarEventsManager.getInstance();
        ArrayList<CalendarEvent> events = manager.readCalendarEvent();
        Utils.androidDataLog(context, TAG, "sending calendar events size " + events.size());
        if (events.size() > 0) {
            DataManager.getInstance().sendCalendarEvents(getUserId(context), events);
        }

    }

    public static void scheduleContactsForImmediateUpload(Context context, boolean b) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        prefs.edit().putBoolean(SCHEDULE_CONTACTS_FOR_IMMEDIATE_UPLOAD, b).commit();
    }

    public static void sendContacts(Context context) {
        if (hasPermission(context, "android.permission.READ_CONTACTS")) {
            ArrayList<AndroidContact> contactList = new ArrayList<AndroidContact>();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0", null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    AndroidContact contact = new AndroidContact();
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    int timesContacted = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
                    int isStarred = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.STARRED));
                    String last_contacted = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));

                    contact.setName(name);
                    contact.setId(id);
                    contact.setTimesContacted(timesContacted);
                    contact.setIsStarred(isStarred);
                    contact.setLastContacted(last_contacted);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = ?", new String[]{id}, null);
                    // String[] columnNamesa = pCur.getColumnNames();
                    while (pCur.moveToNext()) {
                        AndroidContact.PhoneNumber phoneNumber = new AndroidContact.PhoneNumber();

                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String typeStr = phoneNumber.getTypeString(type);
                        phoneNumber.setPhoneNumber(phone);
                        phoneNumber.setType(typeStr);
                        contact.addPhone(phoneNumber);
                    }
                    pCur.close();

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                            + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        contact.setEmail(email);
                    }
                    emailCur.close();
                    ;
                    contactList.add(contact);
                }
            }
            cur.close();

            List<List<AndroidContact>> contactPartition = partition(contactList, 50);
            Utils.androidDataLog(context, TAG, "sending contacts partitions " + contactPartition.size());
            for (List<AndroidContact> contacts : contactPartition) {
                DataManager.getInstance().sendUserContacts(getUserId(context), contacts);
            }
        }
    }

    public static final void setSMSSent(Context context, boolean value) {
        getUserSharedPreferences(context).edit().putBoolean(Constants.SMS_SENT, value).commit();
    }

    public static synchronized void androidDataLog(Context context, String tag, String message) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            File filesDir = context.getExternalFilesDir("logs");
            if (filesDir!=null) {
                try {
                    if (!filesDir.exists()) {
                        filesDir.mkdirs();
                    }
                    File logFile = new File(filesDir.getCanonicalPath() + File.separator + "androidData.log");
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }

                    FileWriter writer = new FileWriter(logFile, true);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    writer.append(dateFormat.format(new Date(System.currentTimeMillis())) + " :");
                    writer.append(tag + " - ");
                    writer.append(message);
                    writer.append("\n");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized boolean getAndClearLocationArray(int length, Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        String result = prefs.getString(DAILY_LOCATION_ARRAY, null);
        //Check if previous pending array has been sent
        boolean hasPendingDataToSend = prefs.getString(DAILY_LOCATION_ARRAY_PENDING, null) == null ? false : true;

        Utils.androidDataLog(context, TAG, "Checking location array. size = " + length);

        if (result != null) {
            Utils.androidDataLog(context, TAG, "moving storage array to pending queue");
            if (!hasPendingDataToSend) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(DAILY_LOCATION_ARRAY, null);
                editor.putString(DAILY_LOCATION_ARRAY_PENDING, result);
                editor.commit();
                return true;
            }
            Utils.androidDataLog(context, TAG, "pending queue is still present. deferring.");
            if (length > 96) {
                Utils.androidDataLog(context, TAG, "storage array exceeds quota. force replace pending queue");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(DAILY_LOCATION_ARRAY, null);
                editor.putString(DAILY_LOCATION_ARRAY_PENDING, result);
                editor.commit();
                return true;
            }
        } else {
            Utils.androidDataLog(context, TAG, "storage array is empty.");
        }

        return false;
    }


    public static int addToLocationArray(Context context, Location lastKnownLocation) {
        if (lastKnownLocation != null) {
            SharedPreferences prefs = getUserSharedPreferences(context);
            String locationArrayString = prefs.getString(DAILY_LOCATION_ARRAY, null);
            JSONArray locationArr = null;
            if (locationArrayString == null) {
                locationArr = new JSONArray();
            } else {
                try {
                    locationArr = new JSONArray(locationArrayString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            locationArr.put(DataHelper.getLocationJsonSingle(lastKnownLocation));
            prefs.edit().putString(DAILY_LOCATION_ARRAY, locationArr.toString()).commit();
            return locationArr.length();
        } else {
            Log.d(TAG, "Last known location not present.");
        }
        return 0;
    }

    public static void getLastKnownLocation(Context context, final OnLocationFoundListener listener) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = lm.getBestProvider(crit, true);
        if (provider == null) {
            //no coarse provider
            Log.d(TAG, "no COARSE PROVIDER switching to GPS");
            crit.setAccuracy(Criteria.ACCURACY_LOW);
            provider = lm.getBestProvider(crit, true);
        }

        String gpsprovider = lm.getBestProvider(crit, true);

        if (provider != null) {
            Location location = lm.getLastKnownLocation(provider);
            if (location == null) {

                Log.d(TAG, "no location present. Requesting for update");

                if (gpsprovider != null) {
                    Location gpslocation = lm.getLastKnownLocation(gpsprovider);
                    if (gpslocation!=null) {
                        listener.onLocationFound(location);
                        return;
                    }
                }

                lm.requestSingleUpdate(crit, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        listener.onLocationFound(location);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {
                    }

                    @Override
                    public void onProviderDisabled(String s) {
                    }
                }, null);
            } else {
                if (location.getTime() < System.currentTimeMillis() - 1000 * 60 * 60) {
                    Log.d(TAG,"Location out of date");
                    lm.requestSingleUpdate(crit, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            listener.onLocationFound(location);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {
                        }

                        @Override
                        public void onProviderDisabled(String s) {
                        }
                    }, null);
                } else {
                    listener.onLocationFound(location);
                }
            }
        }
    }

    public static final void setLocationSent(Context context, boolean value) {
        getUserSharedPreferences(context).edit().putBoolean(LOCATION_SENT, value).commit();
    }

    public static boolean isScheduledForImmediateUpload(Context context) {
        SharedPreferences prefs = getUserSharedPreferences(context);
        return prefs.getBoolean(SCHEDULE_FOR_IMMEDIATE_UPLOAD, false);
    }

    public static void uploadPendingData(final Context context) {
        if (isScheduledForImmediateUpload(context)) {
            Utils.androidDataLog(context,TAG,"Location data pending upload detected.");
            Log.d(TAG, "pending upload detected");
            if (isOnline(context)) {
                Utils.androidDataLog(context,TAG,"Online. Checking services.");
                DataManager.getInstance().getMemberById(Utils.getUserId(context), new UserListener() {
                    @Override
                    public void onSuccess(User user) {
                        Utils.androidDataLog(context,TAG,"services online.");
                        Log.d(TAG, "sending pending upload");
                        scheduleForImmediateUpload(context, false);
                        LocationUpdateSender.transmitPayload(context);
                    }

                    @Override
                    public void onError(int statusCode, String rawResponse) {

                    }


                });

            } else {
                Utils.androidDataLog(context,TAG, "not online.");
            }

        }

        if (isContactsScheduledForImmediateUpload(context)) {
            Log.d(TAG, "pending upload detected");
            if (isOnline(context)) {
                DataManager.getInstance().getMemberById(Utils.getUserId(context), new UserListener() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "sending pending upload");
                        scheduleContactsForImmediateUpload(context, false);
                        WeeklyUpdateAlarmReceiver.transmitPayload(context);
                    }

                    @Override
                    public void onError(int statusCode, String rawResponse) {

                    }


                });

            }
        }
    }

}
