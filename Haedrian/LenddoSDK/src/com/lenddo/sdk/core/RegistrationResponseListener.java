package com.lenddo.sdk.core;

/**
 * Created by joseph on 8/13/14.
 */
public interface RegistrationResponseListener {
    void onError(int statusCode, String rawResponse);
}
