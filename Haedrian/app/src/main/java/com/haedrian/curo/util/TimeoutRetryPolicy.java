package com.haedrian.curo.util;

import com.android.volley.DefaultRetryPolicy;

/**
 * Created by Logan on 6/2/2015.
 */
public class TimeoutRetryPolicy extends DefaultRetryPolicy {

    public TimeoutRetryPolicy() {
        super(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

}
