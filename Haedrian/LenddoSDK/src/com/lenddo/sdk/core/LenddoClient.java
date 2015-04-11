package com.lenddo.sdk.core;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lenddo.sdk.core.formbuilder.LenddoConfig;
import com.lenddo.sdk.http.LenddoBasicHttp;
import com.lenddo.sdk.http.LenddoHttpInterface;
import com.lenddo.sdk.http.OnLenddoQueryCompleteListener;
import com.lenddo.sdk.listeners.UserListener;
import com.lenddo.sdk.models.ApplicationDefinition;
import com.lenddo.sdk.models.User;
import com.lenddo.sdk.utils.DataHelper;
import com.lenddo.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by joseph on 8/8/14.
 */
public class LenddoClient {

    private static final String TAG = LenddoClient.class.getName();

    public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss 'GMT' yyyy";
    private static final String PREFIX = "LENDDO";
    private static final String AUTHENTICATION = "/Authentication";
    private static final String MEMBER = "/Member";
    private static final String APPLICATION_DEFINITION = "/ApplicationDefinition/%s";
    private static final String APPLICATION_DEFINITION_MARKET = "/%s";
    private static final String APPLICATION_DEFINITION_FIELDS_ONLY = "?fields_only=1&show_visibleIf=1";
    private static final String MEMBERS_PATH_ID = "/Member/%s";

    // Scopes
    private static final String MEMBER_IMAGES = "member_images";
    private static final String MEMBER_SOCIAL_CREDENTIALS = "member_social_credentials";
    private static final String MEMBER_CONNECTED_NETWORKS = "member_connected_networks";
    private static final String MEMBER_SCORE = "member_score";
    private static final String MEMBER_REMOVE_STATUS = "member_remove_status";

    // Social service paths
    private static final String ANDROID_DATA = "/AndroidData/%s";

    private final Credentials memberServiceCredentials;
    private final Credentials productServiceCredentials;
    private final Credentials socialServiceCredentials;

    public LenddoHttpInterface getHttpHandler() {
        return httpHandler;
    }

    public void setHttpHandler(LenddoHttpInterface httpHandler) {
        this.httpHandler = httpHandler;
    }

    LenddoHttpInterface httpHandler;

    public LenddoClient(Credentials memberServiceCredentials, Credentials productServiceCredentials, Credentials socialServiceCredentials) {
        this.memberServiceCredentials = memberServiceCredentials;
        this.productServiceCredentials = productServiceCredentials;
        this.socialServiceCredentials = socialServiceCredentials;
        this.httpHandler = new LenddoBasicHttp();
    }

    public static final String getAuthDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = Calendar.getInstance(Locale.US).getTime();
        String authDate = dateFormatGmt.format(d);
        return authDate;
    }

    private HashMap<String, String> getAuthHeader(String method, String date, String body, String path, String secret, String serviceUserId) {
        HashMap<String, String> authHeader = new HashMap<String, String>();
        authHeader.put("Authorization", getAuthValue(method, date, body, path, secret, serviceUserId));
        authHeader.put("Date", date);
        return authHeader;
    }

    private <T> Map<String, String> getAuthHeader(String method, String date, Class<T> clazz, T entity,
                                                  String path, String secret, String serviceUserId) {
        Gson gson = new Gson();
        String body = gson.toJson(entity, clazz);
        return getAuthHeader(method, date, body, path, secret, serviceUserId);
    }

    private String getAuthValue(String method, String date, String body, String path, String secret, String serviceUserId) {
        String md5Body = TextUtils.isEmpty(body) ? "" : Utils.md5(body);
        String toSign = Utils.getSignString(method, md5Body, date, path);

        String sha1 = Utils.hmacSha1(secret, toSign);

        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(" ");
        sb.append(serviceUserId);
        sb.append(":");
        sb.append(sha1);
        return sb.toString();
    }

    /**
     * Authenticates a user with lenddo using email and password
     *
     * @param email
     * @param password
     * @param listener
     */
    public void authenticate(String email, String password, final UserResponseListener listener) {
        JSONObject userData = DataHelper.getCreateUserData(email, password);
        String url = LenddoConfig.getMemberServiceEndpoint() + AUTHENTICATION;
        String date = getAuthDate();
        HashMap<String, String> headers = getAuthHeader("POST", date, userData.toString(), AUTHENTICATION,
                memberServiceCredentials.getSecretKey(),
                memberServiceCredentials.getUserId());
        httpHandler.query(url, headers, null, userData.toString(), LenddoConstants.HTTP_POST, null, new OnLenddoQueryCompleteListener() {
            @Override
            public void onComplete(String rawResponse) {
                Log.d(TAG, "response = " + rawResponse);
                try {
                    User user = DataHelper.getUserFromJson(new JSONObject(rawResponse));
                    listener.onSuccess(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int statusCode, String rawResponse) {
                listener.onError(statusCode, rawResponse);
            }
        });
    }

    private String getMemberScope() {
        StringBuilder sb = new StringBuilder();
        sb.append("scope[]=");
        sb.append(MEMBER_IMAGES);
        sb.append("&scope[]=");
        sb.append(MEMBER_CONNECTED_NETWORKS);
        sb.append("&scope[]=");
        sb.append(MEMBER_SOCIAL_CREDENTIALS);
        sb.append("&scope[]=");
        sb.append(MEMBER_SCORE);
        return sb.toString();
    }


    public void getMemberById(String id, final UserListener userListener) {
        String path = String.format(MEMBERS_PATH_ID, id);
        String scope = getMemberScope();
        // String scope = MEMBER_IMAGES + "," + MEMBER_SCORE;
        // String url = Config.MEMBERS_SERVICE_ENDPOINT + path +
        // "?extended_record=true";
        String url = LenddoConfig.getMemberServiceEndpoint() + path + "?" + scope + "";
        String date = getAuthDate();

        HashMap<String, String> headers = getAuthHeader("GET", date, "", path,
                memberServiceCredentials.getSecretKey(),
                memberServiceCredentials.getUserId());

        httpHandler.query(url, headers, null, "", LenddoConstants.HTTP_GET, null, new OnLenddoQueryCompleteListener() {
            @Override
            public void onComplete(String rawResponse) {
                try {
                    User user = DataHelper.getUserFromJson(new JSONObject(rawResponse));
                    userListener.onSuccess(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int statusCode, String rawResponse) {
                userListener.onError(statusCode, rawResponse);
            }
        });
    }


    public void sendAndroidData(String userId, JSONObject data, final AndroidDataResponseListener listener) {
        String path = String.format(ANDROID_DATA, userId);
        String url = LenddoConfig.getSocialServiceEndpoint() + path;
        String date = getAuthDate();
        HashMap<String, String> headers = getAuthHeader("POST", date, data.toString(), path,
                socialServiceCredentials.getSecretKey(),
                socialServiceCredentials.getUserId());
        httpHandler.query(url, headers, null, data.toString(), LenddoConstants.HTTP_POST, null, new OnLenddoQueryCompleteListener() {
            @Override
            public void onComplete(String rawResponse) {
                try {
                    listener.onSuccess(new JSONObject(rawResponse));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int statusCode, String rawResponse) {
                listener.onError(statusCode, rawResponse);
            }
        });
    }

    /**
     * Registers a user using a facebook token
     *
     * @param user
     * @param facebookToken
     * @param listener
     */
    public void registerUserFB(final User user, String facebookToken, long expiration, final RegistrationResponseListener listener) {
        JSONObject userData = DataHelper.getRegisterUserDataFB(user, facebookToken, expiration);
        String url = LenddoConfig.getMemberServiceEndpoint() + MEMBER;
        String date = getAuthDate();
        HashMap<String, String> headers = getAuthHeader("POST", date, userData.toString(), MEMBER,
                memberServiceCredentials.getSecretKey(),
                memberServiceCredentials.getUserId());
        httpHandler.query(url, headers, null, userData.toString(), LenddoConstants.HTTP_POST, null, new OnLenddoQueryCompleteListener() {
            @Override
            public void onComplete(String rawResponse) {
                Log.d(TAG, "response = " + rawResponse);
            }

            @Override
            public void onError(int statusCode, String rawResponse) {
                listener.onError(statusCode, rawResponse);
            }
        });
    }

    public void getLoanApplicationDefinition(String product, String market, final ApplicationDefinitionListener listener) {
        String path = String.format(APPLICATION_DEFINITION, product);
        if (market != null && !"".equals(market)) {
            path += String.format(APPLICATION_DEFINITION_MARKET, market);
        }

        String url = LenddoConfig.getProductsServiceEndpoint() + path + APPLICATION_DEFINITION_FIELDS_ONLY;
        String date = getAuthDate();

        HashMap<String, String> headers = getAuthHeader("GET", date, "", path,
                productServiceCredentials.getSecretKey(),
                productServiceCredentials.getUserId());
        httpHandler.query(url, headers, null, null, LenddoConstants.HTTP_GET, null, new OnLenddoQueryCompleteListener() {
            @Override
            public void onComplete(String rawResponse) {
                try {
                    ApplicationDefinition definition = DataHelper.getApplicationDefinitionFromJson(new JSONObject(rawResponse));
                    listener.onComplete(definition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int statusCode, String rawResponse) {
                listener.onError(statusCode, rawResponse);
            }
        });

    }


}
