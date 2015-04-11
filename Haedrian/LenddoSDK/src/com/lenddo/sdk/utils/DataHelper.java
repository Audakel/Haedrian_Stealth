package com.lenddo.sdk.utils;

import android.content.pm.PackageInfo;
import android.provider.SyncStateContract;
import android.util.Log;

import com.lenddo.sdk.core.LenddoConstants;
import com.lenddo.sdk.models.ApplicationDefinition;
import com.lenddo.sdk.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joseph on 8/11/14.
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



    public static JSONObject getCreateUserData(String email, String password) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(KEY_EMAIL, email);
            obj.put(KEY_PASSWORD, password);
            return obj;
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
        }
        return obj;
    }

    public static User getUserFromJson(JSONObject json) {
        User user = new User();
        try {
            user.setId(json.optString(KEY_ID));
            user.setLoginEmail(json.optString(KEY_LOGIN_EMAIL));
            JSONObject authObject = json.optJSONObject(KEY_AUTHENTICATION_TOKEN);
            if (authObject!=null) {
                user.setAuthToken(authObject.optString(KEY_TOKEN));
            }

            JSONObject basic = json.optJSONObject(KEY_BASIC);
            if (basic != null) {
                user.setGender(basic.optString(KEY_GENDER));
                JSONObject birthday = basic.optJSONObject(KEY_BIRTHDAY);
                if (birthday != null) {
                    user.setDayBirth(birthday.optInt(KEY_DAY));
                    user.setMonthBirth(birthday.optInt(KEY_MONTH));
                    user.setYearBirth(birthday.optInt(KEY_YEAR));
                }
            }

            JSONObject name = json.optJSONObject(KEY_NAME);
            if (name != null) {
                user.setFirstName(name.optString(KEY_FIRST_NAME));
                user.setLastName(name.optString(KEY_LAST_NAME));
            }

            if (json.has(KEY_LOGIN_EMAIL)) {
                user.setLoginEmail(json.optString(KEY_LOGIN_EMAIL));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return user;

    }

    public static JSONObject getRegisterUserDataFB(User user, String token, long expires) {

        JSONObject obj = new JSONObject();

        try {
            JSONObject fbLoginObj = new JSONObject();
            JSONObject tokenObj = new JSONObject();
            tokenObj.put(KEY_ACCESS_TOKEN, token);

            long expires_seconds =  (expires - System.currentTimeMillis()) / 1000;
            tokenObj.put(KEY_EXPIRES, expires_seconds);
            fbLoginObj.put(KEY_TOKEN, tokenObj);
            fbLoginObj.put(KEY_COUNTRY, user.getCountry());
            fbLoginObj.put(KEY_EXPIRATION_TIMESTAMP, expires_seconds);
            JSONObject metaObj = new JSONObject();
            metaObj.put(KEY_PROTOCOL, "OAuth 2.0");
            metaObj.put(KEY_CREATED, System.currentTimeMillis()/1000);
            fbLoginObj.put(KEY_META, metaObj);
            fbLoginObj.put(KEY_IDENTITY, user.getIdentity());

            obj.put(KEY_FB_LOGIN, fbLoginObj);

            JSONObject loginObj = new JSONObject();
            loginObj.put(KEY_EMAIL, user.getLoginEmail());
            // loginObj.put(KEY_PASSWORD, "123");
            obj.put(KEY_LOGIN, loginObj);
            JSONObject nameObj = new JSONObject();
            nameObj.put(KEY_FIRST_NAME, user.getFirstName());
            nameObj.put(KEY_LAST_NAME, user.getLastName());
            obj.put(KEY_NAME, nameObj);
            JSONObject basicObj = new JSONObject();
            basicObj.put(KEY_GENDER, user.getGender().toLowerCase().equals(LenddoConstants.MALE) ? LenddoConstants.MALE : LenddoConstants.FEMALE);
            JSONObject birthdayObj = new JSONObject();
            birthdayObj.put(KEY_DAY, user.getDayBirth());
            birthdayObj.put(KEY_MONTH, user.getMonthBirth());
            birthdayObj.put(KEY_YEAR, user.getYearBirth());
            basicObj.put(KEY_BIRTHDAY, birthdayObj);
            basicObj.put(KEY_LOCALE, user.getLocale());
            obj.put(KEY_BASIC, basicObj);
            JSONObject contactObj = new JSONObject();
            JSONObject phoneObj = new JSONObject();
            phoneObj.put(KEY_MOBILE, user.getMobilePhone());
            contactObj.put(KEY_PHONE, phoneObj);
            obj.put(KEY_CONTACT, contactObj);
            return obj;
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
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

    public static ApplicationDefinition getApplicationDefinitionFromJson(JSONObject jsonObject) {
        return null;
    }
}
