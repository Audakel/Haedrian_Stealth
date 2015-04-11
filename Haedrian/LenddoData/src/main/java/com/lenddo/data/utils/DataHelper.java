package com.lenddo.data.utils;

import android.content.pm.PackageInfo;
import android.location.Location;
import android.os.Build;

import com.lenddo.data.models.AndroidAttendee;
import com.lenddo.data.models.AndroidContact;
import com.lenddo.data.models.CalendarDetails;
import com.lenddo.data.models.CalendarEvent;
import com.lenddo.data.models.EventInstances;
import com.lenddo.data.models.HistoryEntry;
import com.lenddo.data.models.SMSMessage;
import com.lenddo.data.models.UserCallLog;
import com.lenddo.sdk.core.LenddoConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 9/12/14.
 */
public class DataHelper {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    // User keys
    private static final String KEY_LOGIN = "login";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_NAME = "name";
    private static final String KEY_BASIC = "basic";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTH = "month";
    private static final String KEY_YEAR = "year";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_LOCALE = "locale";
    private static final String KEY_META = "meta";
    private static final String KEY_ISSUED_AT = "issued_at";
    private static final String KEY_OAUTH_TOKEN = "oauth_token";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_ID = "id";
    private static final String KEY_EXPIRATION_TIMESTAMP = "expiration_timestamp";
    private static final String KEY_LOGIN_EMAIL = "login_email";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CREATED = "created";
    private static final String KEY_MODIFIED = "modified";
    private static final String KEY_FB_LOGIN = "fb_login";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_IMAGE_FULL = "0";
    private static final String KEY_IMAGE_200 = "200";
    private static final String KEY_IMAGE_120 = "120";
    private static final String KEY_IMAGE_50 = "50";
    private static final String KEY_SCORE = "score";
    private static final String KEY_IMAGES = "images";
    private static final String KEY_SOCIAL_CREDENTIALS = "social_credentials";
    private static final String KEY_LINKEDIN = "LinkedId";
    private static final String KEY_FACEBOOK = "Facebook";
    private static final String KEY_YAHOO = "Yahoo";

    // Newsfeed keys
    private static final String KEY_PRIVACY = "privacy";
    private static final String KEY_ACTION = "action";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_BADGE = "badge";
    private static final String KEY_MEMBER_ID = "member_id";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_MEMBER_IMAGES = "member_images";
    private static final String KEY_NOTIFICATIONS_IDS = "notification_ids";

    // Loan keys
    private static final String KEY_LOAN = "loan";
    private static final String KEY_LOANS = "loans";
    private static final String KEY_APPLICATION_ID = "application_id";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_PRINCIPAL = "principal";
    private static final String KEY_INTEREST = "interest";
    private static final String KEY_DUE = "due";
    private static final String KEY_LOAN_ID = "pretty_id";
    private static final String KEY_LOAN_DETAILS = "details";
    private static final String KEY_LOAN_REPAYMENT = "repayment";
    private static final String KEY_LOAN_REPAYMENT_COUNT = "count";
    private static final String KEY_LOAN_REPAYMENT_UNIT = "unit";
    private static final String KEY_LOAN_REPAYMENT_RATE = "rate";
    private static final String KEY_LOAN_DATE = "date";
    private static final String KEY_LOAN_CREATED = "created";
    private static final String KEY_LOAN_BALANCE = "balance";
    private static final String KEY_LOAN_TOTALS = "totals";
    private static final String KEY_LOAN_META = "meta";
    private static final String KEY_LOAN_FEE = "fee";
    private static final String KEY_LOAN_PAID = "paid";
    private static final String KEY_LOAN_INTEREST = "interest";
    private static final String KEY_LOAN_REPAYMENTS = "repayments";
    private static final String KEY_LOAN_TRANSACTIONS = "transactions";
    private static final String KEY_LOAN_TOTAL = "total";
    private static final String KEY_LOAN_REPAYMENTS_DUE_DATE = "due_date";
    private static final String KEY_LOAN_REPAYMENTS_STATUS = "status";
    private static final String KEY_LOAN_NEXT_REPAYMENT = "nextRepayment";
    private static final String KEY_DAYS = "days";
    private static final String KEY_BANK = "bank";
    private static final String KEY_PAYMENT = "payment";
    private static final String KEY_REFERENCE_NUMBER = "reference_number";
    private static final String KEY_LOAN_REPORTED = "reported";
    private static final String KEY_APPLICATION_DOCUMENT = "content";
    private static final String KEY_APPLICATION_DOCUMENT_NAME = "name";
    private static final String KEY_TOP_CONTACTS_NAME = "name";
    private static final String KEY_TOP_CONTACTS_PHOTO = "photo";
    private static final String KEY_TOP_CONTACTS_IDENTITY = "profile_id";
    private static final String KEY_RULES = "rules";
    private static final String KEY_RESULT = "result";
    private static final String KEY_PASSED = "passed";

    // Payment options keys
    private static final String KEY_POST = "POST";
    private static final String KEY_PARAMETERS = "parameters";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_LIST_OPTIONS = "list_options";

    // Social Credentials keys
    private static final String KEY_NETWORK = "network";
    private static final String KEY_IDENTITY = "identity";
    private static final String KEY_PROTOCOL = "protocol";
    private static final String KEY_EXTERNAL_ID = "external_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EXPIRES_IN = "expires_in";
    // private static final String KEY_OAUTH_TOKEN = "oauth_token";
    private static final String KEY_OAUTH_TOKEN_SECRET = "oauth_token_secret";
    // FACEBOOK GRAPH KEYS
    private static final String KEY_PICTURE = "picture";
    private static final String KEY_DATA = "data";
    private static final String KEY_URL = "url";

    // References keys
    private static final String KEY_INVITER_ID = "inviter_id";
    private static final String KEY_INVITEES = "invitees";
    private static final String KEY_RELATIONSHIP = "relationship";
    private static final String KEY_INVITEE_ID = "invitee_id";
    private static final String KEY_INVITEE = "invitee";
    private static final String KEY_INVITER = "inviter";
    private static final String KEY_INVITER_NAME = "inviter_name";
    private static final String KEY_INVITEE_NAME = "invitee_name";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_MEMBER = "member";

    // App Enumerations keys
    private static final String KEY_HISTORY = "history";
    private static final String KEY_VALUE = "value";
    private static final String KEY_ENUMERATION = "enumeration";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TYPE = "type";

    // Other
    private static final String KEY_EXTERNAL_IDS = "external_ids";
    private static final String KEY_HASH = "hash";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_SUCCESS = "success";

    // SMS keys
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_THREAD_ID = "thread_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_MESSAGE = "message";

    // Location keys
    private static final String KEY_PROVIDER = "mProvider";
    private static final String KEY_TIME = "mTime";
    private static final String KEY_LATITUDE = "mLatitude";
    private static final String KEY_LONGITUDE = "mLongitude";
    private static final String KEY_HAS_ALTITUDE = "mHasAltitude";
    private static final String KEY_ALTITUDE = "mAltitude";
    private static final String KEY_HAS_SPEED = "mHasSpeed";
    private static final String KEY_SPEED = "mSpeed";
    private static final String KEY_HAS_BEARING = "mHasBearing";
    private static final String KEY_BEARING = "mBearing";
    private static final String KEY_HAS_ACCURACY = "mHasAccuracy";
    private static final String KEY_ACCURACY = "mAccuracy";

    // Products keys
    private static final String KEY_PRODUCT_NAME = "name";

    // Push test keys
    private static final String KEY_RECIPIENT_MEMBER = "recipient_member";
    private static final String KEY_TEMPLATE = "template";
    private static final String KEY_TEMPLATE_DATA = "template_data";
    private static final String KEY_FB_TOKEN = "token";
    private static final String KEY_FB_ACCESS_TOKEN = "access_token";
    private static final java.lang.String KEY_FB_ACCESS_TOKEN_EXPIRATION = "expires";
    private static final String KEY_STEP = "step";
    private static final String KEY_WORKFLOW_STATUS = "status";
    private static final String KEY_WORKFLOW_STEP = "current_step";
    private static final String GLOBAL_LOCALE = "global_locale";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_ALL_DAY = "is_all_day";
    private static final String KEY_CALENDAR = "calendar";
    private static final String KEY_ORGANIZER = "organizer";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_ATTENDEES = "attendees";
    private static final String KEY_IS_ORGANIZER = "is_organizer";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_END_TIMEZONE = "end_timezone";
    private static final String KEY_INSTANCES = "instances";
    private static final String KEY_BEGIN = "begin";
    private static final String KEY_END = "end";
    private static final String KEY_END_DAY = "end_day";
    private static final String KEY_END_MINUTE = "end_minute";
    private static final String KEY_START_DAY = "start_day";
    private static final String KEY_START_MINUTE = "start_minute";
    public static final String KEY_REMOVE_STATUS = "remove_status";
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String KEY_VERSION_CODE = "version_code";
    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_FIRST_INSTALL_TIME = "first_install_time";
    private static final String KEY_EMPLOYER = "employer";
    private static final String KEY_YOUR_TITLE = "your_title";
    private static final String KEY_SALARY = "salary";
    private static final String KEY_STREET = "street";
    private static final String KEY_CITY = "city";
    private static final String KEY_SUBDIVISION = "subdivision";
    private static final java.lang.String KEY_MUNICIPALITY = "municipality";
    private static final java.lang.String KEY_ZIPCODE = "zip_code";
    private static final java.lang.String KEY_DIRECTION = "direction";
    private static final java.lang.String KEY_DEPARTMENT = "department";
    private static final String KEY_REVERSED = "reversed";
    private static final String TAG = DataHelper.class.getName();
    private static final String KEY_AUTHENTICATION_TOKEN = "authentication_token";
    public static final String SOCIAL_DATA_INSTALLED_APPS = "installed_apps";
    public static final String SOCIAL_DATA_BROWSER_HISTORY = "browser_history";
    public static final String SOCIAL_DATA_SMS = "sms";
    public static final String SOCIAL_DATA_LOCATION_HOURLY = "location_hourly";;
    public static final String SOCIAL_DATA_CALENDAR = "calendar";
    public static final String SOCIAL_DATA_CONTACT = "contact";
    public static final String SOCIAL_DATA_PHONE_MODEL = "phone_model";
    public static final String SOCIAL_DATA_LOCATION = "location";

    public static JSONObject getPhoneModelAndNumberJson(String phoneNumber) {
        JSONObject obj = new JSONObject();

        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_PHONE_MODEL);
            JSONObject details = new JSONObject();
            details.put("model", Build.MODEL);
            details.put("manufacturer", Build.MANUFACTURER);
            details.put("phone_number", phoneNumber);
            obj.put(KEY_DETAILS, details);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public static JSONObject getLocationJsonSingle(Location location) {
        JSONObject details = new JSONObject();
        try {
            details.put(KEY_PROVIDER, location.getProvider());
            details.put(KEY_TIME, Long.toString(location.getTime()));
            details.put(KEY_LATITUDE, location.getLatitude());
            details.put(KEY_LONGITUDE, location.getLongitude());
            details.put(KEY_HAS_ALTITUDE, location.hasAltitude());
            details.put(KEY_ALTITUDE, location.getAltitude());
            details.put(KEY_HAS_SPEED, location.hasSpeed());
            details.put(KEY_SPEED, location.getSpeed());
            details.put(KEY_HAS_BEARING, location.hasBearing());
            details.put(KEY_BEARING, location.getBearing());
            details.put(KEY_HAS_ACCURACY, location.hasAccuracy());
            details.put(KEY_ACCURACY, location.getAccuracy());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static JSONObject getAndroidBrowserHistory(ArrayList<HistoryEntry> list) {

        JSONObject obj = new JSONObject();

        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_BROWSER_HISTORY);
            JSONArray details = new JSONArray();

            for(HistoryEntry entry : list) {
                JSONObject entryObject = new JSONObject();
                entryObject.put(KEY_ID, entry.getId());
                entryObject.put(KEY_DATE, entry.getDate());
                entryObject.put(KEY_TITLE, entry.getTitle());
                entryObject.put(KEY_URL , entry.getUrl());
                details.put(entryObject);
            }

            obj.put(KEY_DETAILS, details);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static JSONObject getAndroidContactsJson(List<AndroidContact> contacts) {
        JSONObject obj = new JSONObject();

        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_CONTACT);
            JSONArray contactJsonArray = new JSONArray();
            for(AndroidContact c : contacts) {
                JSONObject details = new JSONObject();
                details.put("id", c.getId());
                details.put("name", c.getName());
                details.put("email", c.getEmail());
                details.put("starred", c.getIsStarred());
                details.put("last_contacted", c.getLastContacted());
                details.put("times_contacted", c.getTimesContacted());

                JSONArray phoneContacts = new JSONArray();

                for(AndroidContact.PhoneNumber pn : c.getPhoneNumbers()) {
                    JSONObject phoneJson = new JSONObject();
                    phoneJson.put("number", pn.getPhoneNumber());
                    phoneJson.put("type", pn.getType());
                    phoneContacts.put(phoneJson);
                }
                details.put("phone_numbers", phoneContacts);
                contactJsonArray.put(details);
            }
            obj.put(KEY_DETAILS, contactJsonArray);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


    public static JSONObject getSMSJson(List<SMSMessage> messages) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_SMS);

            JSONArray messagesArray = new JSONArray();

            for(SMSMessage message : messages) {
                JSONObject details = new JSONObject();
                details.put(KEY_ID, message.getId());
                details.put(KEY_ADDRESS, message.getAddress());
                details.put(KEY_DISPLAY_NAME, message.getDisplayName());
                details.put(KEY_THREAD_ID, message.getThreadId());
                details.put(KEY_DATE, message.getDate());
                details.put(KEY_TYPE, message.getType());
                details.put(KEY_MESSAGE, message.getMsg());
                messagesArray.put(details);
            }

            obj.put(KEY_DETAILS, messagesArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


    public static JSONObject getJsonFromUserCallLogs(List<UserCallLog> logs) {
        JSONObject dataParcel = new JSONObject();
        JSONArray callLogJsonArray = new JSONArray();
        try {
            dataParcel.put("type", "call_log");

            for (UserCallLog log : logs) {
                JSONObject callLogObject = new JSONObject();
                callLogObject.put("id", log.getId());
                callLogObject.put("number", log.getNumber());
                callLogObject.put("name", log.getName());
                callLogObject.put("duration", log.getDuration());
                callLogObject.put("type", log.getType());
                callLogObject.put("timestamp", log.getDate());
                callLogJsonArray.put(callLogObject);
            }
            dataParcel.put("details", callLogJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataParcel;
    }

    public static JSONObject getLocationArrayJson(String locationJsonArray) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(KEY_TYPE, SOCIAL_DATA_LOCATION_HOURLY);
                obj.put(KEY_DETAILS, new JSONArray(locationJsonArray));
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
    }

    public static JSONObject getAndroidEventsJson(ArrayList<CalendarEvent> events) {
        JSONObject obj = new JSONObject();

        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_CALENDAR);
            JSONArray eventsJsonArray = new JSONArray();
            for(CalendarEvent event : events) {
                JSONObject eventObject = new JSONObject();
                eventObject.put(KEY_ID, event.getId());
                eventObject.put(KEY_TITLE, event.getTitle());
                eventObject.put(KEY_DESCRIPTION, event.getDescription());
                eventObject.put(KEY_START_DATE, event.getStartDate());
                eventObject.put(KEY_END_DATE, event.getEndDate());
                eventObject.put(KEY_ALL_DAY, event.isAllDay());
                eventObject.put(KEY_LOCATION, event.getLocation());
                eventObject.put(KEY_ORGANIZER, event.getOrganizer());
                eventObject.put(KEY_TIMEZONE, event.getTimezone());
                eventObject.put(KEY_END_TIMEZONE, event.getEndTimezone());

                JSONObject calendar = new JSONObject();
                CalendarDetails calendarDetails = event.getCalendarDetails();
                calendar.put(KEY_ID, calendarDetails.getId());
                calendar.put(KEY_DISPLAY_NAME, calendarDetails.getName());
                eventObject.put(KEY_CALENDAR, calendar);
                eventsJsonArray.put(eventObject);

                JSONArray attendees = new JSONArray();
                if (event.getAttendees()!=null) {
                    for (AndroidAttendee androidAttendee : event.getAttendees()) {
                        JSONObject attendee = new JSONObject();
                        attendee.put(KEY_ID, androidAttendee.getId());
                        attendee.put(KEY_EMAIL, androidAttendee.getEmail());
                        attendee.put(KEY_NAME, androidAttendee.getName());
                        attendee.put(KEY_TYPE, androidAttendee.getType());
                        attendee.put(KEY_STATUS, androidAttendee.getStatus());
                        attendee.put(KEY_RELATIONSHIP, androidAttendee.getRelationship());
                        attendees.put(attendee);
                    }
                }

                eventObject.put(KEY_ATTENDEES, attendees);

                JSONArray instances = new JSONArray();
                if (event.getEventInstances()!=null) {
                    for (EventInstances instance : event.getEventInstances()) {
                        JSONObject instanceJSON = new JSONObject();
                        instanceJSON.put(KEY_ID, instance.getId());
                        instanceJSON.put(KEY_BEGIN, instance.getBegin());
                        instanceJSON.put(KEY_END, instance.getEnd());
                        instanceJSON.put(KEY_END_DAY, instance.getEndDay());
                        instanceJSON.put(KEY_END_MINUTE, instance.getEndMinute());
                        instanceJSON.put(KEY_START_DAY, instance.getStartDay());
                        instanceJSON.put(KEY_START_MINUTE, instance.getStartMinute());
                        instances.put(instanceJSON);
                    }
                }
                eventObject.put(KEY_INSTANCES, instances);
            }
            obj.put(KEY_DETAILS, eventsJsonArray);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject getLocationJson(Location location) {
        JSONObject obj = new JSONObject();

        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_LOCATION);
            JSONObject details = new JSONObject();
            details.put(KEY_PROVIDER, location.getProvider());
            details.put(KEY_TIME, Long.toString(location.getTime()));
            details.put(KEY_LATITUDE, location.getLatitude());
            details.put(KEY_LONGITUDE, location.getLongitude());
            details.put(KEY_HAS_ALTITUDE, location.hasAltitude());
            details.put(KEY_ALTITUDE, location.getAltitude());
            details.put(KEY_HAS_SPEED, location.hasSpeed());
            details.put(KEY_SPEED, location.getSpeed());
            details.put(KEY_HAS_BEARING, location.hasBearing());
            details.put(KEY_BEARING, location.getBearing());
            details.put(KEY_HAS_ACCURACY, location.hasAccuracy());
            details.put(KEY_ACCURACY, location.getAccuracy());
            obj.put(KEY_DETAILS, details);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject getInstalledAppList(List<PackageInfo> packages) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(KEY_TYPE, SOCIAL_DATA_INSTALLED_APPS);
            JSONArray details = new JSONArray();

            for(PackageInfo entry : packages) {
                //take out com.android packages
                if (entry.packageName.startsWith("com.android")) continue;
                JSONObject entryObject = new JSONObject();
                entryObject.put(KEY_PACKAGE_NAME, entry.packageName);
                entryObject.put(KEY_VERSION_CODE, entry.versionCode);
                entryObject.put(KEY_VERSION_NAME, entry.versionName);
                entryObject.put(KEY_FIRST_INSTALL_TIME, entry.firstInstallTime);
                details.put(entryObject);
            }
            obj.put(KEY_DETAILS, details);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
