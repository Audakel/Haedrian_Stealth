package com.haedrian.haedrian.Application;

import android.app.Application;
import android.content.Intent;
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
        mRequestQueue = Volley.newRequestQueue(this); // 'this' is Context


        FlurryAgent.setLogEnabled(false);

        FlurryAgent.setVersionName(getString(R.string.version));
        FlurryAgent.init(this, ApplicationConstants.MY_FLURRY_API_KEY);

        //Parse setup
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "QdakXDOx8Ta6W4g2kfdWkGyN0CS9CjxppjirJnqN", "CyXcvlXI1I0qfxAdhoYT0dlnHpNn0RSn5NoS1CB3");

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

    public static String getExchangeRate() {

        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            
        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {

        }

        return "";
    }


}
