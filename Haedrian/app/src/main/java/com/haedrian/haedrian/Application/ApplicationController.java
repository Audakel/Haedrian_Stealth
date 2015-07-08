package com.haedrian.haedrian.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.haedrian.haedrian.R;
import com.parse.Parse;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by audakel on 3/21/15.
 */
public class ApplicationController extends Application {

    public static final String TAG = "VolleyPatterns";
    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static ApplicationController sInstance;
    private RequestQueue mRequestQueue;
    private boolean loggedIn;
    private static String token = "";
    private static File cacheDir;
    private static long balanceTimestamp = 0L;
    private static long buySellTimestamp = 0L;
    private static long transactionTimestamp = 0L;
    private static long homeScreenTimestamp = 0L;
    private static SharedPreferences sp;

    private static String currencyUS;
    private static String currencyPHP;

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized ApplicationController getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;
        mRequestQueue = Volley.newRequestQueue(this); // 'this' is Contex

        JodaTimeAndroid.init(this);

        FlurryAgent.setLogEnabled(false);

        FlurryAgent.setVersionName(getString(R.string.version));
        FlurryAgent.init(this, ApplicationConstants.MY_FLURRY_API_KEY);

        // Set up default currency
        sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (Locale.getDefault().equals(Locale.US)) {
            editor.putString("currency", "USD");
        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            editor.putString("currency", "PHP");
        }

        currencyUS = getResources().getString(R.string.currency_USD);
        currencyPHP = getResources().getString(R.string.currency_PHP);


        editor.apply();

        cacheDir = getCacheDir();
    }

    private void checkedLoggedInState() {
        loggedIn = true;
        if (loggedIn) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static String getToken() { return token; }

    public static void setToken(String userToken) { token = userToken; }



    public static void cacheJSON(JSONObject response, String name) {
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(cacheDir, name) + "cacheFile.srl"));
            out.writeObject(response.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getCachedJSON(String name) {
        JSONObject cachedResponse = new JSONObject();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(cacheDir, name) + "cacheFile.srl"));
            String jsonSerialized = (String) in.readObject();
            cachedResponse = new JSONObject(jsonSerialized);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cachedResponse;
    }

    public static void resetTimestamps() {
        balanceTimestamp = 0L;
        buySellTimestamp = 0L;
        transactionTimestamp = 0L;
        homeScreenTimestamp = 0L;
    }

    public static long getBalanceTimestamp() {
        return balanceTimestamp;
    }

    public static void setBalanceTimestamp(long balanceTimestampTemp) {
        balanceTimestamp = balanceTimestampTemp;
    }

    public static long getHomeScreenTimestamp() { return homeScreenTimestamp; }

    public static void setHomeScreenTimestamp(long homeScreenTimestampTemp) {
        homeScreenTimestamp = homeScreenTimestampTemp;
    }

    public static long getBuySellTimestamp() {
        return buySellTimestamp;
    }

    public static void setBuySellTimestamp(long buySellTimestampTemp) {
        buySellTimestamp = buySellTimestampTemp;
    }

    public static long getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public static void setTransactionTimestamp(long transactionTimestampTemp) {
        transactionTimestamp = transactionTimestampTemp;
    }

    public static String getSetCurrency() {
        return sp.getString("currency", Currency.getInstance(Locale.getDefault()).getCurrencyCode());
    }

    public static String getSetCurrencySign() {
        if (sp.getString("currency", "").equals("USD")) {
            return currencyUS;
        }
        else if (sp.getString("currency", "").equals("PHP")) {
            return currencyPHP;
        }
        else {
            return "";
        }
    }


}
