package com.lenddo.sdk.core;

import com.lenddo.sdk.models.User;

import org.json.JSONObject;

/**
 * Created by joseph on 9/12/14.
 */
public interface AndroidDataResponseListener {
    void onSuccess(JSONObject object);

    void onError(int statusCode, String rawResponse);
}
